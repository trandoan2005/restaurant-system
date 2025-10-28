package com.example.restaurant_system.controller;

import com.example.restaurant_system.Service.PaymentService;
import com.example.restaurant_system.config.MomoConfig;
import com.example.restaurant_system.dto.ApiResponse;
import com.example.restaurant_system.dto.MomoPaymentResponse;
import com.example.restaurant_system.dto.PaymentRequest;
import com.example.restaurant_system.dto.PaymentResponse;
import com.example.restaurant_system.entity.Payment;
import com.example.restaurant_system.enums.PaymentMethod;
import com.example.restaurant_system.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final MomoConfig momoConfig;

    // ✅ Lấy danh sách tất cả payment
    @GetMapping
    public ResponseEntity<ApiResponse> getAllPayments() {
        try {
            List<Payment> payments = paymentService.getAll();
            return ResponseEntity.ok(ApiResponse.success("✅ Lấy danh sách payment thành công", payments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi: " + e.getMessage()));
        }
    }

    // ✅ Lấy chi tiết payment theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getPaymentById(@PathVariable Long id) {
        try {
            return paymentService.getById(id)
                    .map(payment -> ResponseEntity.ok(ApiResponse.success("✅ Lấy payment thành công", payment)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi: " + e.getMessage()));
        }
    }

    // ✅ Xóa payment theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePayment(@PathVariable Long id) {
        try {
            paymentService.delete(id);
            return ResponseEntity.ok(ApiResponse.success("✅ Xóa payment thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi xóa: " + e.getMessage()));
        }
    }

    // ✅ Tạo thanh toán online và gọi Momo API
    @PostMapping("/create-online")
    public ResponseEntity<ApiResponse> createOnlinePayment(@RequestBody PaymentRequest request) {
        try {
            Payment payment = paymentService.createOnlinePayment(
                request.getPaymentMethod(),
                request.getAmount(),
                request.getOrderId(),
                request.getCustomerName(),
                request.getCustomerPhone()
            );

            MomoPaymentResponse momoResponse = paymentService.initiateMomoPayment(payment);

            PaymentResponse response = new PaymentResponse();
            response.setSuccess(true);
            response.setMessage("✅ Tạo thanh toán online thành công");
            response.setRequestId(payment.getRequestId());
            response.setOrderId(payment.getOrderId());
            response.setPaymentUrl(momoResponse.getPayUrl());
            response.setQrCodeUrl("https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + momoResponse.getPayUrl());

            return ResponseEntity.ok(ApiResponse.success("✅ Tạo thanh toán online thành công", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi tạo thanh toán: " + e.getMessage()));
        }
    }

    // ✅ MOCK: Checkout nhanh cho testing (tạo Payment rồi mark COMPLETED ngay lập tức)
    @PostMapping("/mock-checkout")
    public ResponseEntity<ApiResponse> mockCheckout(@RequestBody PaymentRequest request) {
        try {
            // Tạo payment (PENDING)
            Payment payment = paymentService.createOnlinePayment(
                    request.getPaymentMethod(),
                    request.getAmount(),
                    request.getOrderId(),
                    request.getCustomerName(),
                    request.getCustomerPhone()
            );

            // Gán transactionId giả và mark completed
            String fakeTx = "MOCKTX-" + System.currentTimeMillis();
            payment.setTransactionId(fakeTx);
            payment = paymentService.processSuccessfulPayment(fakeTx, payment.getRequestId());

            return ResponseEntity.ok(ApiResponse.success("✅ Mock checkout thành công", payment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi mock checkout: " + e.getMessage()));
        }
    }

    // ✅ Webhook xử lý kết quả từ Momo
    @PostMapping("/webhook/momo")
    public ResponseEntity<?> momoWebhook(@RequestBody Map<String, Object> payload) {
        try {
            String requestId = (String) payload.get("requestId");
            String transactionId = (String) payload.get("transId");
            String resultCode = String.valueOf(payload.get("resultCode"));

            if ("0".equals(resultCode)) {
                paymentService.processSuccessfulPayment(transactionId, requestId);
            } else {
                paymentService.processFailedPayment(requestId, "Momo trả về lỗi: " + resultCode);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ Kiểm tra trạng thái thanh toán
    @GetMapping("/status/{requestId}")
    public ResponseEntity<ApiResponse> getPaymentStatus(@PathVariable String requestId) {
        try {
            PaymentStatus status = paymentService.getPaymentStatus(requestId);
            return ResponseEntity.ok(ApiResponse.success("✅ Trạng thái thanh toán", status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi kiểm tra trạng thái: " + e.getMessage()));
        }
    }
}
