package com.mclabs.reactive.rest.controller;

import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mclabs.reactive.rest.model.Product;
import com.mclabs.reactive.rest.model.ProductEvent;
import com.mclabs.reactive.rest.repository.ProductRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
public class ProductController {
	private ProductRepository productRepository;

	public ProductController(ProductRepository repo) {
		this.productRepository = repo;
	}

	// return more than one product.
	@GetMapping
	public Flux<Product> getAllProducts() {
		return productRepository.findAll();
	}

	// return mono of product
	@GetMapping("{id}")
	public Mono<ResponseEntity<Product>> getProduct(@PathVariable String id) {
		return productRepository.findById(id).map(product -> ResponseEntity.ok(product))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Product> saveProduct(@RequestBody Product product) {
		return productRepository.save(product);
	}

	@PutMapping("{id}")
	public Mono<ResponseEntity<Product>> updateProduct(@PathVariable(value = "id") String id,
			@RequestBody Product product) {
		return productRepository.findById(id).flatMap(existingProduct -> {
			existingProduct.setName(product.getName());
			existingProduct.setPrice(product.getPrice());
			return productRepository.save(existingProduct);
		}).map(updateProduct -> ResponseEntity.ok(updateProduct)).defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@DeleteMapping("{id}")
	public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable(value = "id") String id) {
		return productRepository.findById(id)
				.flatMap(existingProduct -> productRepository.delete(existingProduct)
						.then(Mono.just(ResponseEntity.ok().<Void>build())))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@DeleteMapping
	public Mono<Void> deleteAllProducts() {
		return productRepository.deleteAll();
	}

	@GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ProductEvent> getProductEvents() {
		return Flux.interval(Duration.ofSeconds(1)).map(val -> new ProductEvent(val, "Product Event"));
	}
}
