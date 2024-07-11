package com.hermes.inventory_service.service;

import com.hermes.inventory_service.dto.InventoryRequest;
import com.hermes.inventory_service.dto.InventoryResponse;
import com.hermes.inventory_service.dto.ProductResponse;
import com.hermes.inventory_service.exception.ResourceNotFoundException;
import com.hermes.inventory_service.model.Inventory;
import com.hermes.inventory_service.repository.InventoryRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WebClient.Builder webClientBuilder;

    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "productServiceFallback")
    public void createInventory(String skuCode, InventoryRequest inventoryRequest) {
        ProductResponse productResponse = webClientBuilder.build()
                .get()
                .uri("http://localhost:8080/api/products/sku/" + skuCode)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    throw new ResourceNotFoundException("Product not found with SKU code: " + skuCode);
                })
                .bodyToMono(ProductResponse.class)
                .block();

        if (inventoryRepository.findBySkuCode(skuCode).isPresent()) {
            throw new IllegalArgumentException("Inventory with SKU code " + skuCode + " already exists");
        }

        Inventory inventory = Inventory.builder()
                .skuCode(productResponse.skuCode())
                .quantity(inventoryRequest.quantity())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        inventoryRepository.save(inventory);
        log.info("Inventory for product {} is saved", inventory.getSkuCode());
    }

    public List<InventoryResponse> getAllInventory() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream().map(this::mapToInventoryResponse).toList();
    }

    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "productServiceFallback")
    public InventoryResponse getInventoryBySkuCode(String skuCode) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with SKU code: " + skuCode));
        return mapToInventoryResponse(inventory);
    }

    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "productServiceFallback")
    public InventoryResponse updateInventory(String skuCode, InventoryRequest inventoryRequest) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with SKU code: " + skuCode));

        ProductResponse productResponse = webClientBuilder.build()
                .get()
                .uri("http://localhost:8080/api/products/sku/" + skuCode)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    throw new ResourceNotFoundException("Product not found with SKU code: " + skuCode);
                })
                .bodyToMono(ProductResponse.class)
                .block();

        inventory.setQuantity(inventoryRequest.quantity());
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);

        return mapToInventoryResponse(inventory);
    }

    public void deleteInventory(String skuCode) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with SKU code: " + skuCode));
        inventoryRepository.delete(inventory);
    }

    private InventoryResponse mapToInventoryResponse(Inventory inventory) {
        return new InventoryResponse(inventory.getId(), inventory.getSkuCode(), inventory.getQuantity());
    }
}