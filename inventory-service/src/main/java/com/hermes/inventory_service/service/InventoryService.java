package com.hermes.inventory_service.service;

import com.hermes.inventory_service.dto.InventoryResponse;
import com.hermes.inventory_service.dto.ProductResponse;
import com.hermes.inventory_service.exception.ResourceNotFoundException;
import com.hermes.inventory_service.model.Inventory;
import com.hermes.inventory_service.repository.InventoryRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

import static com.hermes.inventory_service.config.RabbitMQConfig.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WebClient.Builder webClientBuilder;

    @RabbitListener(queues = PRODUCT_CREATED_QUEUE)
    public void handleProductCreated(String skuCode) {
        log.info("Received message for product creation with SKU code: {}", skuCode);

        Inventory inventory = Inventory.builder()
                .skuCode(skuCode)
                .quantity(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        inventoryRepository.save(inventory);
        log.info("Inventory for product with SKU code {} is created with quantity 0", skuCode);
    }

    @RabbitListener(queues = PRODUCT_UPDATED_QUEUE)
    public void handleProductUpdated(SkuUpdateMessage skuUpdateMessage) {
        log.info("Received message for product update with old SKU code: {}", skuUpdateMessage.getOldSkuCode());

        Inventory inventory = inventoryRepository.findBySkuCode(skuUpdateMessage.getOldSkuCode())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with SKU code: " + skuUpdateMessage.getOldSkuCode()));

        inventory.setSkuCode(skuUpdateMessage.getNewSkuCode());
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);

        log.info("Inventory for product with old SKU code {} is updated to new SKU code {}", skuUpdateMessage.getOldSkuCode(), skuUpdateMessage.getNewSkuCode());
    }

    @RabbitListener(queues = PRODUCT_DELETED_QUEUE)
    public void handleProductDeleted(String skuCode) {
        log.info("Received message for product deletion with SKU code: {}", skuCode);

        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with SKU code: " + skuCode));

        inventoryRepository.delete(inventory);
        log.info("Inventory for product with SKU code {} is deleted", skuCode);
    }

    public List<InventoryResponse> getAllInventory() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream().map(this::mapToInventoryResponse).toList();
    }

    public InventoryResponse getInventoryBySkuCode(String skuCode) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with SKU code: " + skuCode));
        return mapToInventoryResponse(inventory);
    }

    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "productServiceFallback")
    public InventoryResponse updateQuantity(String skuCode, int quantity) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with SKU code: " + skuCode));

        // Simulate a call to ProductService (this can be replaced with actual WebClient call)
        ProductResponse productResponse = webClientBuilder.build()
                .get()
                .uri("http://localhost:8080/api/products/sku/" + skuCode)
                .retrieve()
                .bodyToMono(ProductResponse.class)
                .block();

        inventory.setQuantity(quantity);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);

        return mapToInventoryResponse(inventory);
    }

    private InventoryResponse mapToInventoryResponse(Inventory inventory) {
        return new InventoryResponse(inventory.getId(), inventory.getSkuCode(), inventory.getQuantity());
    }
}