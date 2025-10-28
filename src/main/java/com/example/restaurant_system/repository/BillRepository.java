package com.example.restaurant_system.repository;

import com.example.restaurant_system.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    // ✅ THÊM: Tìm bill theo số hóa đơn
    Optional<Bill> findByBillNumber(String billNumber);
    
    // ✅ THÊM: Tìm bill theo order
    Optional<Bill> findByOrderId(Long orderId);
    
    // ✅ THÊM: Tìm bill trong khoảng thời gian
    List<Bill> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}