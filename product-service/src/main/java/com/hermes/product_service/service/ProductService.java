package com.hermes.product_service.service;

import com.hermes.product_service.dto.ProductRequest;
import com.hermes.product_service.dto.ProductResponse;
import com.hermes.product_service.exception.ResourceNotFoundException;
import com.hermes.product_service.model.Product;
import com.hermes.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void createProduct(ProductRequest productRequest) {
        // Check if product with the same SKU code already exists
        Optional<Product> existingProduct = productRepository.findBySkuCode(productRequest.skuCode());
        if (existingProduct.isPresent()) {
            log.error("Product with SKU code {} already exists", productRequest.skuCode());
            throw new IllegalArgumentException("Product with SKU code " + productRequest.skuCode() + " already exists");
        }

        // If not exists, create and save the new product
        Product product = Product.builder()
                .skuCode(productRequest.skuCode())
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .imageUrl(productRequest.imageUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::mapToProductResponse).toList();
    }

    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToProductResponse(product);
    }

    public ProductResponse getProductBySkuCode(String skuCode) {
        Product product = productRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU code: " + skuCode));
        return mapToProductResponse(product);
    }

    public ProductResponse updateProduct(String id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Check if the new SKU code already exists
        if (!product.getSkuCode().equals(productRequest.skuCode())) {
            Optional<Product> existingProductWithNewSku = productRepository.findBySkuCode(productRequest.skuCode());
            if (existingProductWithNewSku.isPresent()) {
                throw new IllegalArgumentException("Product with SKU code " + productRequest.skuCode() + " already exists");
            }
        }

        product.setSkuCode(productRequest.skuCode());
        product.setName(productRequest.name());
        product.setDescription(productRequest.description());
        product.setPrice(productRequest.price());
        product.setImageUrl(productRequest.imageUrl());
        product.setUpdatedAt(LocalDateTime.now());

        productRepository.save(product);
        return mapToProductResponse(product);
    }

    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public ProductResponse getProductByName(String name) {
        Product product = productRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with name: " + name));
        return mapToProductResponse(product);
    }

    public ProductResponse getProductByPrice(Double price) {
        Product product = productRepository.findByPrice(price)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with price: " + price));
        return mapToProductResponse(product);
    }

    private ProductResponse mapToProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSkuCode(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}