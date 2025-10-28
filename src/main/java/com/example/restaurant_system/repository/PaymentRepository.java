package com.example.restaurant_system.repository;

import com.example.restaurant_system.entity.Payment;
import com.example.restaurant_system.enums.PaymentMethod;
import com.example.restaurant_system.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByMethod(PaymentMethod method);
    List<Payment> findByIssuedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // ✅ THÊM: Các method mới cho thanh toán online
    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findByRequestId(String requestId);
    List<Payment> findByOrderId(String orderId);
}