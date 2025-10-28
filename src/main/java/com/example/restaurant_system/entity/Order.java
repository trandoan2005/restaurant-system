package com.example.restaurant_system.entity;

import com.example.restaurant_system.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order { // 🔥 XÓA @JsonIgnoreProperties

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // 🔥 SỬA: Dùng @JsonIgnore thay vì @JsonIgnoreProperties
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // 🔥 SỬA: Dùng @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    @JsonIgnore
    private TableEntity table;

    private Double totalAmount;
    private String notes;

    // 🔥 SỬA: Dùng @JsonIgnore và FetchType.LAZY
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderDetail> orderDetails = new ArrayList<>();

    // ✅ Hàm tiện ích để thêm chi tiết đơn hàng
    public void addOrderDetail(OrderDetail detail) {
        orderDetails.add(detail);
        detail.setOrder(this);
    }

    // ✅ Hàm tiện ích để xóa chi tiết đơn hàng
    public void removeOrderDetail(OrderDetail detail) {
        orderDetails.remove(detail);
        detail.setOrder(null);
    }

    // 🔥 XÓA CÁC METHOD LỖI NÀY:
    // public void setTableId(String string) {
    //     throw new UnsupportedOperationException("Unimplemented method 'setTableId'");
    // }
    //
    // public void setCustomerNote(String string) {
    //     throw new UnsupportedOperationException("Unimplemented method 'setCustomerNote'");
    // }
    //
    // public Object getCustomerNote() {
    //     throw new UnsupportedOperationException("Unimplemented method 'getCustomerNote'");
    // }
    //
    // public Object getTableId() {
    //     throw new UnsupportedOperationException("Unimplemented method 'getTableId'");
    // }

    // 🔥 THÊM: Method toString đơn giản để debug
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", createdAt=" + createdAt +
                '}';
    }
}