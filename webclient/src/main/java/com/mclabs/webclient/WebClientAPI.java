package com.mclabs.webclient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Make sure reactive-rest-api/reactive-api-function-handler is running on
 * background
 */

public class WebClientAPI {
	private WebClient webClient;

	public static void main(String args[]) {
		WebClientAPI api = new WebClientAPI();

		api.postNewProduct().thenMany(api.getAllProducts()) // get all product
				.take(1) // take one product
				.flatMap(p -> api.updateProduct(p.getId(), "White Tea", 0.99)) // update the selected product: return
																				// mono
				.flatMap(p -> api.deleteProduct(p.getId())) // delete: return Mono
				.thenMany(api.getAllProducts()) // get all product: return Flex<Product>
				.thenMany(api.getAllEvents()).subscribeOn(Schedulers.newSingle("myThread")) // request should execute on
																							// another thread.
				.subscribe(System.out::println); // subscribe to publisher. subscribe will trigger all sequence

//	        try {
//	            Thread.sleep(5000);
//	        } catch(Exception e) { }
	}

	public WebClientAPI() {
		this.webClient = WebClient.builder().baseUrl("http://localhost:8080/products").build();
	}

	private Mono<ResponseEntity<Product>> postNewProduct() {
		return webClient.post().body(Mono.just(new Product(null, "Jasmine Tea", 1.99)), Product.class)
				.exchangeToMono(response -> response.toEntity(Product.class))
				.doOnSuccess(o -> System.out.println("**********POST " + o));
	}

	private Flux<Product> getAllProducts() {
		return webClient.get().retrieve().bodyToFlux(Product.class)
				.doOnNext(o -> System.out.println("**********GET: " + o));
	}

	private Mono<Product> updateProduct(String id, String name, double price) {
		return webClient.put().uri("/{id}", id).body(Mono.just(new Product(null, name, price)), Product.class)
				.retrieve().bodyToMono(Product.class).doOnSuccess(o -> System.out.println("**********UPDATE " + o));
	}

	private Mono<Void> deleteProduct(String id) {
		return webClient.delete().uri("/{id}", id).retrieve().bodyToMono(Void.class)
				.doOnSuccess(o -> System.out.println("**********DELETE " + o));
	}

	private Flux<ProductEvent> getAllEvents() {
		return webClient.get().uri("/events").retrieve().bodyToFlux(ProductEvent.class);
	}
}
