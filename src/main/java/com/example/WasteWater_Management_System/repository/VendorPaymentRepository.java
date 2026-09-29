package com.example.WasteWater_Management_System.repository;

import com.example.WasteWater_Management_System.models.VendorPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorPaymentRepository extends JpaRepository<VendorPayment, Long> {
    List<VendorPayment> findByVendorBillId(Long vendorBillId);
    Optional<VendorPayment> findByPaymentReference(String paymentReference);
}
