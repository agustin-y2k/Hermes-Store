package com.hermes.inventory_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InventoryRequest(

        @NotNull(message = "Quantity is mandatory")
        @Min(value = 0, message = "Quantity must be zero or positive")
        Integer quantity) {
}