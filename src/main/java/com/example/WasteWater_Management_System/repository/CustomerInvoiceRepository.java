package com.example.WasteWater_Management_System.repository;

import com.example.WasteWater_Management_System.models.CustomerInvoice;
import com.example.WasteWater_Management_System.models.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerInvoiceRepository extends JpaRepository<CustomerInvoice, Long> {
    List<CustomerInvoice> findByStatus(InvoiceStatus status);
    List<CustomerInvoice> findByFactoryId(Long factoryId);
    Optional<CustomerInvoice> findByInvoiceNumber(String invoiceNumber);

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0.0) FROM CustomerInvoice i WHERE i.status <> 'DRAFT'")
    Double sumTotalInvoicedRevenue();

    @Query("SELECT COALESCE(SUM(i.paidAmount), 0.0) FROM CustomerInvoice i")
    Double sumTotalPaidRevenue();

    @Query("SELECT COALESCE(SUM(i.balanceAmount), 0.0) FROM CustomerInvoice i WHERE i.status <> 'PAID'")
    Double sumTotalOutstandingBalance();

    @Query("SELECT COALESCE(SUM(i.subtotal), 0.0) FROM CustomerInvoice i WHERE i.status <> 'DRAFT'")
    Double sumTotalTreatmentServiceFees();

    @Query("SELECT COALESCE(SUM(i.surchargeAmount), 0.0) FROM CustomerInvoice i WHERE i.status <> 'DRAFT'")
    Double sumTotalComplianceSurcharges();
}
