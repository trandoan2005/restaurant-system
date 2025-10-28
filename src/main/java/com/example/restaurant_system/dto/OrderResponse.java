package com.example.restaurant_system.dto;

import com.example.restaurant_system.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private Long id;
    private OrderStatus status;
    private Double totalAmount;
    private String notes;
    private LocalDateTime createdAt;
    private String tableNumber;
    private List<OrderDetailDTO> orderDetails;

    // ✅ SỬA: Constructor nhận entity.Order thay vì dto.Order
    public OrderResponse(com.example.restaurant_system.entity.Order order) {
        this.id = order.getId();
        this.status = order.getStatus();
        this.totalAmount = order.getTotalAmount();
        this.notes = order.getNotes();
        this.createdAt = order.getCreatedAt();
        this.tableNumber = order.getTable() != null ? order.getTable().getName() : "Unknown";
        // Chuyển orderDetails sang DTO nếu cần
    }
}