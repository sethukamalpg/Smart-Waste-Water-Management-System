package com.example.WasteWater_Management_System.repository;

import com.example.WasteWater_Management_System.models.Product;
import com.example.WasteWater_Management_System.models.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductType(ProductType productType);
    List<Product> findByActive(Boolean active);
    Optional<Product> findByProductName(String productName);
}
