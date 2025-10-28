package com.example.restaurant_system.repository;

import com.example.restaurant_system.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    
    // ✅ Tìm order details theo order ID
    List<OrderDetail> findByOrderId(Long orderId);
    
    // ✅ Tìm order details theo product ID
    List<OrderDetail> findByProductId(Long productId);
    
    // ✅ Tìm order details theo trạng thái kitchen
    List<OrderDetail> findByKitchenStatus(String kitchenStatus);
    
    // ✅ Đếm order details theo trạng thái kitchen
    Long countByKitchenStatus(String kitchenStatus);
    
    // ✅ Tìm order details theo order ID và product ID
    List<OrderDetail> findByOrderIdAndProductId(Long orderId, Long productId);
}