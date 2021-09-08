package com.mclabs.reactive.function.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.mclabs.reactive.function.model.Product;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}
