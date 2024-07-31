package com.inventory_service.inventory_service.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory_service.inventory_service.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    List<Inventory> findBySkuCodeIn(List<String> skuCode);
}
