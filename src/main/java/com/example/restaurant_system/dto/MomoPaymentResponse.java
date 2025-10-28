package com.example.restaurant_system.dto;

import lombok.Data;

@Data
public class MomoPaymentResponse {
    private String partnerCode;
    private String requestId;
    private String orderId;
    private Long amount;
    private Long responseTime;
    private String message;
    private Integer resultCode;
    private String payUrl;
    private String qrCodeUrl;
    private String deeplink;
    private String signature;

    // ✅ Thêm trường transId để tránh lỗi biên dịch
    private String transId;
}
