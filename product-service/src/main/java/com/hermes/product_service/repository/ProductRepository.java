package com.hermes.product_service.repository;

import com.hermes.product_service.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByName(String name);
    Optional<Product> findByPrice(Double price);
    Optional<Product> findBySkuCode(String skuCode);
}