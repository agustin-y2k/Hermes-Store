package com.hermes.product_service.repository;

import com.hermes.product_service.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(Product.class);
    }

    @Test
    void testFindBySkuCode() {
        // given
        Product product = Product.builder()
                .skuCode("123ABC")
                .name("Test Product")
                .description("Description")
                .price(100.0)
                .imageUrl("http://example.com/image.png")
                .build();
        mongoTemplate.save(product);

        // when
        Optional<Product> foundProduct = productRepository.findBySkuCode("123ABC");

        // then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Test Product");
    }
}