package com.example.restaurant_system.dto;

import lombok.Data;

@Data
public class MomoPaymentRequest {
    private String partnerCode;
    private String partnerName;
    private String storeId;
    private String requestId;
    private Long amount;
    private String orderId;
    private String orderInfo;
    private String redirectUrl;
    private String ipnUrl;
    private String lang;
    private String requestType;
    private Boolean autoCapture;
    private String extraData;
    private String orderGroupId;
    private String signature;
}