package com.example.restaurant_system.Service;

import com.example.restaurant_system.config.MomoConfig;
import com.example.restaurant_system.dto.MomoPaymentRequest;
import com.example.restaurant_system.dto.MomoPaymentResponse;
import com.example.restaurant_system.entity.Payment;
import com.example.restaurant_system.enums.PaymentMethod;
import com.example.restaurant_system.enums.PaymentStatus;
import com.example.restaurant_system.repository.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MomoConfig momoConfig;
    private final com.example.restaurant_system.repository.OrderRepository orderRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Payment createOnlinePayment(PaymentMethod method, Double amount, String orderId, String customerName, String customerPhone) {
        Payment payment = new Payment();
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTotalAmount(amount);
        payment.setOrderId(orderId != null ? orderId : generateOrderId());
        // ensure requestId is unique even if linked to an existing order
        payment.setRequestId(payment.getOrderId() + "-" + System.currentTimeMillis());
        payment.setCustomerName(customerName);
        payment.setCustomerPhone(customerPhone);
        return paymentRepository.save(payment);
    }

    public MomoPaymentResponse initiateMomoPayment(Payment payment) {
        try {
            String requestType = "captureWallet";
            String orderInfo = "Thanh toán đơn hàng CoffeePOS";
            String amount = String.valueOf(payment.getTotalAmount().longValue());

            String rawSignature = "accessKey=" + momoConfig.getAccessKey()
                    + "&amount=" + amount
                    + "&extraData="
                    + "&ipnUrl=" + momoConfig.getIpnUrl()
                    + "&orderId=" + payment.getOrderId()
                    + "&orderInfo=" + orderInfo
                    + "&partnerCode=" + momoConfig.getPartnerCode()
                    + "&redirectUrl=" + momoConfig.getReturnUrl()
                    + "&requestId=" + payment.getRequestId()
                    + "&requestType=" + requestType;

            String signature = hmacSHA256(rawSignature, momoConfig.getSecretKey());

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("partnerCode", momoConfig.getPartnerCode());
            payload.put("accessKey", momoConfig.getAccessKey());
            payload.put("requestId", payment.getRequestId());
            payload.put("amount", amount);
            payload.put("orderId", payment.getOrderId());
            payload.put("orderInfo", orderInfo);
            payload.put("redirectUrl", momoConfig.getReturnUrl());
            payload.put("ipnUrl", momoConfig.getIpnUrl());
            payload.put("extraData", "");
            payload.put("requestType", requestType);
            payload.put("signature", signature);
            payload.put("lang", "vi");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(payload), headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<MomoPaymentResponse> response = restTemplate.exchange(
                    momoConfig.getEndpoint() + "/v2/gateway/api/create",
                    HttpMethod.POST,
                    entity,
                    MomoPaymentResponse.class
            );

            MomoPaymentResponse momoResponse = response.getBody();

            updatePaymentUrl(payment.getRequestId(), momoResponse.getPayUrl(), momoResponse.getTransId());

            return momoResponse;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gọi Momo API: " + e.getMessage(), e);
        }
    }

    public Payment processSuccessfulPayment(String transactionId, String requestId) {
        Payment payment = null;
        if (transactionId != null) {
            payment = paymentRepository.findByTransactionId(transactionId).orElse(null);
        }
        if (payment == null) {
            try {
                payment = paymentRepository.findByRequestId(requestId).orElse(null);
            } catch (Exception ex) {
                // duplicate results fallback
                java.util.List<Payment> list = paymentRepository.findAllByRequestId(requestId);
                if (list != null && !list.isEmpty()) payment = list.get(0);
            }
        }
        if (payment == null) throw new RuntimeException("Payment not found");

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId(transactionId);
        payment.setCompletedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        // If payment is linked to an order, mark the order as paid
        try {
            if (saved.getOrderId() != null) {
                try {
                    Long orderId = Long.valueOf(saved.getOrderId());
                    com.example.restaurant_system.entity.Order order = orderRepository.findById(orderId).orElse(null);
                    if (order != null) {
                        order.setPaid(true);
                        order.setPaidAt(LocalDateTime.now());
                        orderRepository.save(order);
                    }
                } catch (NumberFormatException ignored) {}
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not update order paid flag: " + e.getMessage());
        }

        // Notify socket server (best-effort)
        notifySocketServerPayment(saved);

        return saved;
    }

    // Notify socket server about successful payment (simple HTTP POST)
    private void notifySocketServerPayment(Payment payment) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("paymentId", payment.getId());
            payload.put("orderId", payment.getOrderId());
            payload.put("status", payment.getStatus().name());
            payload.put("transactionId", payment.getTransactionId());

            RestTemplate rest = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(payload), headers);
            rest.postForEntity("http://localhost:3001/notify-payment", entity, String.class);
        } catch (Exception e) {
            // ignore notification failures in mock mode
            System.out.println("⚠️ Could not notify socket server: " + e.getMessage());
        }
    }

    public Payment processFailedPayment(String requestId, String errorMessage) {
        Payment payment = paymentRepository.findByRequestId(requestId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(PaymentStatus.FAILED);
        payment.setNotes("Thanh toán thất bại: " + errorMessage);

        return paymentRepository.save(payment);
    }

    public Payment updatePaymentUrl(String requestId, String paymentUrl, String transactionId) {
        Payment payment = paymentRepository.findByRequestId(requestId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setPaymentUrl(paymentUrl);
        if (transactionId != null) {
            payment.setTransactionId(transactionId);
        }

        return paymentRepository.save(payment);
    }

    public PaymentStatus getPaymentStatus(String requestId) {
        Payment payment = paymentRepository.findByRequestId(requestId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return payment.getStatus();
    }

    private String generateOrderId() {
        return "MOMO" + System.currentTimeMillis();
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac.init(secretKey);
        byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // ✅ Xóa payment theo ID
    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }

    // ✅ Lấy payment theo ID
    public Optional<Payment> getById(Long id) {
        return paymentRepository.findById(id);
    }

    // ✅ Lấy tất cả payment
    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }
}
