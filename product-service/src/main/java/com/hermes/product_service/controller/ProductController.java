package com.hermes.product_service.controller;

import com.hermes.product_service.dto.ProductRequest;
import com.hermes.product_service.dto.ProductResponse;
import com.hermes.product_service.dto.ErrorResponse;
import com.hermes.product_service.exception.ResourceNotFoundException;
import com.hermes.product_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product API", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<Object> getProductById(@PathVariable String id) {
        try {
            ProductResponse product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/sku/{skuCode}")
    @Operation(summary = "Get product by sku code")
    public ResponseEntity<Object> getProductBySkuCode(@PathVariable String skuCode) {
        try {
            ProductResponse product = productService.getProductBySkuCode(skuCode);
            return ResponseEntity.ok(product);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/name/{productName}")
    @Operation(summary = "Get product by name")
    public ResponseEntity<Object> getProductByName(@PathVariable String productName) {
        try {
            ProductResponse product = productService.getProductByName(productName);
            return ResponseEntity.ok(product);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/price/{price}")
    @Operation(summary = "Get product by price")
    public ResponseEntity<Object> getProductByPrice(@PathVariable Double price) {
        try {
            ProductResponse product = productService.getProductByPrice(price);
            return ResponseEntity.ok(product);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<Object> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        try {
            productService.createProduct(productRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Product created successfully");
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product")
    public ResponseEntity<Object> updateProduct(@PathVariable String id, @Valid @RequestBody ProductRequest productRequest) {
        try {
            ProductResponse updatedProduct = productService.updateProduct(id, productRequest);
            return ResponseEntity.ok(updatedProduct);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product by ID")
    public ResponseEntity<Object> deleteProduct(@PathVariable String id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}

