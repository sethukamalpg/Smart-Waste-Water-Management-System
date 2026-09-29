package com.example.WasteWater_Management_System.repository;

import com.example.WasteWater_Management_System.models.VendorBill;
import com.example.WasteWater_Management_System.models.enums.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorBillRepository extends JpaRepository<VendorBill, Long> {
    List<VendorBill> findBySupplierId(Long supplierId);
    List<VendorBill> findByStatus(BillStatus status);
    Optional<VendorBill> findByBillNumber(String billNumber);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0.0) FROM VendorBill b WHERE b.status <> 'DRAFT'")
    Double sumTotalBillExpenses();

    @Query("SELECT COALESCE(SUM(b.paidAmount), 0.0) FROM VendorBill b")
    Double sumTotalPaidToVendors();

    @Query("SELECT COALESCE(SUM(b.balanceAmount), 0.0) FROM VendorBill b WHERE b.status <> 'PAID'")
    Double sumTotalUnpaidVendorBalances();
}
