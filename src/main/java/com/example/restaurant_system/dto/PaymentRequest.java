package com.example.restaurant_system.dto;

import com.example.restaurant_system.enums.PaymentMethod;
import lombok.Data;

@Data
public class PaymentRequest {
    private PaymentMethod paymentMethod;
    private Double amount;
    private String orderId;
    private String customerName;
    private String customerPhone;
    private String returnUrl; // URL trả về sau thanh toán
}