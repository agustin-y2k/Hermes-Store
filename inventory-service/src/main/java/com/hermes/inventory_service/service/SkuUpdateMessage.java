package com.hermes.inventory_service.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuUpdateMessage {
    private String oldSkuCode;
    private String newSkuCode;
}

