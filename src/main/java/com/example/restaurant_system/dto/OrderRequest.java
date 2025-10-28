package com.example.restaurant_system.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private Long tableId;
    private String customerNote;  // ✅ Đổi từ notes thành customerNote để khớp frontend
    private List<OrderItemRequest> items;
    private Double totalAmount;   // ✅ Thêm field này
    
    // ❌ BỎ: userId, promotionId (frontend không gửi)
    // private Long userId;
    // private Long promotionId;
}