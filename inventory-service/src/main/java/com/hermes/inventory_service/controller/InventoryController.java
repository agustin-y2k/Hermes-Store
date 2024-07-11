package com.hermes.inventory_service.controller;

import com.hermes.inventory_service.dto.InventoryRequest;
import com.hermes.inventory_service.dto.InventoryResponse;
import com.hermes.inventory_service.dto.ErrorResponse;
import com.hermes.inventory_service.exception.ResourceNotFoundException;
import com.hermes.inventory_service.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory API", description = "Inventory management APIs")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @Operation(summary = "Get all inventory")
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        List<InventoryResponse> inventory = inventoryService.getAllInventory();
        return ResponseEntity.ok(inventory);
    }

    @GetMapping("/{skuCode}")
    @Operation(summary = "Get inventory by SKU code")
    public ResponseEntity<Object> getInventoryBySkuCode(@PathVariable String skuCode) {
        try {
            InventoryResponse inventory = inventoryService.getInventoryBySkuCode(skuCode);
            return ResponseEntity.ok(inventory);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{skuCode}")
    @Operation(summary = "Create a new inventory")
    public ResponseEntity<Object> createInventory(@PathVariable String skuCode, @Valid @RequestBody InventoryRequest inventoryRequest) {
        try {
            inventoryService.createInventory(skuCode, inventoryRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Inventory created successfully");
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{skuCode}")
    @Operation(summary = "Update an existing inventory")
    public ResponseEntity<Object> updateInventory(@PathVariable String skuCode, @Valid @RequestBody InventoryRequest inventoryRequest) {
        try {
            InventoryResponse updatedInventory = inventoryService.updateInventory(skuCode, inventoryRequest);
            return ResponseEntity.ok(updatedInventory);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{skuCode}")
    @Operation(summary = "Delete an inventory by SKU code")
    public ResponseEntity<Object> deleteInventory(@PathVariable String skuCode) {
        try {
            inventoryService.deleteInventory(skuCode);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}
