package com.mclabs.webclient2;

import java.time.Duration;

import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

public class UserService {
	private static final String BASE_URL = "https://jsonplaceholder.typicode.com";
	private static final String BROKEN_URL_TEMPLATE = "/broken-url/{id}";
	public static final int DELAY_MILLIS = 100;
	public static final int MAX_RETRY_ATTEMPTS = 3;
	public static final String INVALID_USER_ID = "10";

	private WebClient webClient;

	public UserService() {
		this.webClient = WebClient.builder().baseUrl(BASE_URL).build();
	}

	public static void main(String args[]) {
		UserService apiUserService = new UserService();

		apiUserService.getAllUsers().take(1) // take one product
				.flatMap(p -> apiUserService.getUserByIdAsync(p.getId().toString()))
				.thenEmpty((Publisher<Void>) apiUserService.getUserWithRetry(INVALID_USER_ID))
				.thenEmpty((Publisher<Void>) apiUserService.getUserWithFallback(INVALID_USER_ID))
				.thenEmpty((Publisher<Void>) apiUserService.getUserWithErrorHandling(INVALID_USER_ID))
				.subscribeOn(Schedulers.newSingle("myThread")) // request should execute on
																// another thread.
				.subscribe(System.out::println); // subscribe to publisher. subscribe will trigger all sequence

	}

	private Flux<User> getAllUsers() {
		return webClient.get().uri("/users").retrieve().bodyToFlux(User.class).doOnNext(o -> System.out.println(o));
	}

	private Mono<User> getUserByIdAsync(String id) {
		return webClient.get().uri("/users/{id}", id).retrieve().bodyToMono(User.class)
				.doOnSuccess(o -> System.out.println("*****GET_USER_BY_ID " + o));
	}

	public User getUserWithRetry(String id) {
		return webClient.get().uri(BROKEN_URL_TEMPLATE, id).retrieve().bodyToMono(User.class)
				.retryWhen(Retry.fixedDelay(MAX_RETRY_ATTEMPTS, Duration.ofMillis(DELAY_MILLIS))).block();
	}

	public User getUserWithFallback(String id) {
		return webClient.get().uri(BROKEN_URL_TEMPLATE, id).retrieve().bodyToMono(User.class)
				.doOnError(error -> System.out.println(error)).onErrorResume(error -> Mono.just(new User())).block();
	}

	public User getUserWithErrorHandling(String id) {
		return webClient.get().uri(BROKEN_URL_TEMPLATE, id).retrieve()
				.onStatus(HttpStatus::is4xxClientError,
						error -> Mono.error(new RuntimeException("!!! API not found!!!")))
				.onStatus(HttpStatus::is5xxServerError,
						error -> Mono.error(new RuntimeException("!!! Server is not responding !!! ")))
				.bodyToMono(User.class).block();
	}
}
