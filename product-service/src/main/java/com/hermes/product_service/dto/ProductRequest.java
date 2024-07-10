package com.hermes.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.URL;

public record ProductRequest(
        @NotBlank(message = "SKU code is mandatory")
        String skuCode,

        @NotBlank(message = "Product name is mandatory")
        String name,

        @NotBlank(message = "Product description is mandatory")
        String description,

        @NotNull(message = "Product price is mandatory")
        @PositiveOrZero(message = "Product price must be zero or positive")
        Double price,

        @URL(message = "Image URL must be valid")
        String imageUrl) {
}