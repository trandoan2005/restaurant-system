package com.example.restaurant_system.entity;

import com.example.restaurant_system.enums.PaymentMethod;
import com.example.restaurant_system.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    private Double totalAmount;
    
    // ✅ THÊM: Các field quan trọng cho thanh toán online
    private String transactionId;      // Mã giao dịch từ Momo, ZaloPay
    private String paymentUrl;         // URL thanh toán hoặc QR code
    private String requestId;          // Mã request duy nhất
    private String orderId;            // Mã đơn hàng liên quan
    private String customerName;       // Tên khách hàng
    private String customerPhone;      // SĐT khách hàng
    
    @CreationTimestamp
    private LocalDateTime issuedAt;
    
    private String notes;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ✅ THÊM: Thời gian hoàn thành thanh toán
    private LocalDateTime completedAt;
}