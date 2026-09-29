package com.example.WasteWater_Management_System.repository;

import com.example.WasteWater_Management_System.models.PurchaseOrder;
import com.example.WasteWater_Management_System.models.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findBySupplierId(Long supplierId);
    List<PurchaseOrder> findByStatus(OrderStatus status);
    Optional<PurchaseOrder> findByPoNumber(String poNumber);
}
