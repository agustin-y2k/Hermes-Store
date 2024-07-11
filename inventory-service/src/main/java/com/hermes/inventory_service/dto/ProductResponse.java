package com.hermes.inventory_service.dto;

import java.time.LocalDateTime;

public record ProductResponse(
        String id,
        String skuCode,
        String name,
        String description,
        Double price,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
