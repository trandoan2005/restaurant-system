package com.example.restaurant_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "order_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore   // ⚡ Thêm dòng này để tránh vòng lặp JSON khi trả về Order
    private Order order;

    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private String note;
    private String kitchenStatus; // "PENDING", "PREPARING", "READY", "COMPLETED"
    public Object getProduct() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProduct'");
    }
}
