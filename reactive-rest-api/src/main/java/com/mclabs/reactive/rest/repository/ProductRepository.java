package com.mclabs.reactive.rest.repository;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.mclabs.reactive.rest.model.Product;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {

}
