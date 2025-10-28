package com.example.restaurant_system.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private boolean success;
    private String message;
    private String paymentUrl;
    private String qrCodeUrl;
    private String requestId;
    private String orderId;
}