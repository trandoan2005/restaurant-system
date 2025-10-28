package com.example.restaurant_system.Service;

import com.example.restaurant_system.config.MomoConfig;
import com.example.restaurant_system.dto.MomoPaymentRequest;
import com.example.restaurant_system.dto.MomoPaymentResponse;
import com.example.restaurant_system.dto.MomoWebhookRequest;
import com.example.restaurant_system.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class MomoService {
    
    private final MomoConfig momoConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    
    public MomoPaymentResponse createPayment(Payment payment, String returnUrl) {
        try {
            MomoPaymentRequest momoRequest = buildMomoRequest(payment, returnUrl);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<MomoPaymentRequest> entity = new HttpEntity<>(momoRequest, headers);
            
            String createUrl = momoConfig.getEndpoint() + "/v2/gateway/api/create";
            
            ResponseEntity<MomoPaymentResponse> response = restTemplate.exchange(
                createUrl,
                HttpMethod.POST,
                entity,
                MomoPaymentResponse.class
            );
            
            log.info("Momo API Response: {}", response.getBody());
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Error creating Momo payment: {}", e.getMessage());
            throw new RuntimeException("Failed to create Momo payment: " + e.getMessage());
        }
    }
    
    private MomoPaymentRequest buildMomoRequest(Payment payment, String returnUrl) {
        MomoPaymentRequest request = new MomoPaymentRequest();
        
        request.setPartnerCode(momoConfig.getPartnerCode());
        request.setPartnerName("Test");
        request.setStoreId("MomoTestStore");
        request.setRequestId(payment.getRequestId());
        request.setAmount(payment.getTotalAmount().longValue());
        request.setOrderId(payment.getOrderId());
        request.setOrderInfo("pay with MoMo");
        request.setRedirectUrl(returnUrl != null ? returnUrl : momoConfig.getReturnUrl());
        request.setIpnUrl(momoConfig.getIpnUrl());
        request.setLang("vi");
        request.setRequestType("payWithMethod");
        request.setAutoCapture(true);
        request.setExtraData("");
        request.setOrderGroupId("");
        
        // Tạo signature theo đúng format của Momo
        String rawSignature = String.format(
            "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
            momoConfig.getAccessKey(),
            request.getAmount(),
            request.getExtraData(),
            request.getIpnUrl(),
            request.getOrderId(),
            request.getOrderInfo(),
            request.getPartnerCode(),
            request.getRedirectUrl(),
            request.getRequestId(),
            request.getRequestType()
        );
        
        request.setSignature(computeHmacSha256(rawSignature, momoConfig.getSecretKey()));
        
        log.info("Raw Signature: {}", rawSignature);
        log.info("Momo Request: {}", request);
        
        return request;
    }
    
    private String computeHmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute HMAC-SHA256: " + e.getMessage());
        }
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    public boolean verifySignature(MomoWebhookRequest webhook) {
        try {
            String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s",
                momoConfig.getAccessKey(),
                webhook.getAmount(),
                webhook.getExtraData(),
                webhook.getMessage(),
                webhook.getOrderId(),
                webhook.getOrderType(),
                webhook.getPartnerCode(),
                webhook.getPayType(),
                webhook.getRequestId(),
                webhook.getResponseTime(),
                webhook.getResultCode(),
                webhook.getTransId()
            );
            
            String expectedSignature = computeHmacSha256(rawSignature, momoConfig.getSecretKey());
            return expectedSignature.equals(webhook.getSignature());
        } catch (Exception e) {
            log.error("Error verifying signature: {}", e.getMessage());
            return false;
        }
    }
}