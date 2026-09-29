package com.example.WasteWater_Management_System.repository;

import com.example.WasteWater_Management_System.models.CustomerPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerPaymentRepository extends JpaRepository<CustomerPayment, Long> {
    List<CustomerPayment> findByInvoiceId(Long invoiceId);
    Optional<CustomerPayment> findByPaymentReference(String paymentReference);
}
