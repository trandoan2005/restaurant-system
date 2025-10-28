package com.example.restaurant_system.repository;

import com.example.restaurant_system.entity.Order;
import com.example.restaurant_system.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByTableId(Long tableId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByStatusIn(List<OrderStatus> statuses);
    List<Order> findByUserId(Long userId);
    
    // ✅ THÊM: Method sắp xếp theo thời gian tạo
    List<Order> findAllByOrderByCreatedAtDesc();
}