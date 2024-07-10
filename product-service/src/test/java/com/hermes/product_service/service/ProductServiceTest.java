package com.hermes.product_service.service;

import com.hermes.product_service.dto.ProductRequest;
import com.hermes.product_service.dto.ProductResponse;
import com.hermes.product_service.exception.ResourceNotFoundException;
import com.hermes.product_service.model.Product;
import com.hermes.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProduct() {
        // given
        ProductRequest productRequest = new ProductRequest("123ABC", "Test Product", "Description", 100.0, "http://example.com/image.png");

        Product savedProduct = Product.builder()
                .id("1")
                .skuCode("123ABC")
                .name("Test Product")
                .description("Description")
                .price(100.0)
                .imageUrl("http://example.com/image.png")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // when
        productService.createProduct(productRequest);

        // then
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testGetProductBySkuCode_NotFound() {
        // given
        when(productRepository.findBySkuCode(anyString())).thenReturn(Optional.empty());

        // when, then
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductBySkuCode("123ABC"));
    }

    @Test
    void testGetProductBySkuCode_Found() {
        // given
        Product product = Product.builder()
                .id("1")
                .skuCode("123ABC")
                .name("Test Product")
                .description("Description")
                .price(100.0)
                .imageUrl("http://example.com/image.png")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(productRepository.findBySkuCode(anyString())).thenReturn(Optional.of(product));

        // when
        ProductResponse productResponse = productService.getProductBySkuCode("123ABC");

        // then
        assertThat(productResponse).isNotNull();
        assertThat(productResponse.name()).isEqualTo("Test Product");
    }

    @Test
    void testUpdateProduct() {
        // given
        Product product = Product.builder()
                .id("1")
                .skuCode("123ABC")
                .name("Test Product")
                .description("Description")
                .price(100.0)
                .imageUrl("http://example.com/image.png")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(productRepository.findById(anyString())).thenReturn(Optional.of(product));

        ProductRequest updateRequest = new ProductRequest("123ABC", "Updated Product", "Updated Description", 200.0, "http://example.com/updated_image.png");

        // when
        ProductResponse updatedProduct = productService.updateProduct("1", updateRequest);

        // then
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.name()).isEqualTo("Updated Product");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testDeleteProduct() {
        // given
        when(productRepository.existsById(anyString())).thenReturn(true);

        // when
        productService.deleteProduct("1");

        // then
        verify(productRepository, times(1)).deleteById(anyString());
    }

    @Test
    void testDeleteProduct_NotFound() {
        // given
        when(productRepository.existsById(anyString())).thenReturn(false);

        // when, then
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct("1"));
    }
}