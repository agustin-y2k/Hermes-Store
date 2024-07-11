package com.hermes.inventory_service.dto;

public record InventoryResponse(
        Long id,
        String skuCode,
        Integer quantity) {
}

