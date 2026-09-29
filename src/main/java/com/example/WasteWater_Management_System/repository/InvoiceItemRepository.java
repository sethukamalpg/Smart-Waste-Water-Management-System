package com.example.WasteWater_Management_System.repository;

import com.example.WasteWater_Management_System.models.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    List<InvoiceItem> findByCustomerInvoiceId(Long invoiceId);
}
