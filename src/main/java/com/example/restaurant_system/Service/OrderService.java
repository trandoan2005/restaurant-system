package com.example.restaurant_system.Service;

import com.example.restaurant_system.dto.OrderRequest;
import com.example.restaurant_system.entity.Order;
import com.example.restaurant_system.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Order operations.
 *
 * NOTE: The original implementation file was accidentally placed here. This interface
 * restores the correct type (OrderService) used across controllers and other services.
 */
public interface OrderService {
    Order createOrder(OrderRequest orderRequest);
    List<Order> getAllOrders();
    Optional<Order> getOrderById(Long id);
    Order updateOrder(Long id, Order updatedOrder);
    void deleteOrder(Long id);
    Order updateOrderStatus(Long id, OrderStatus status);
    List<Order> getOrdersByStatus(OrderStatus status);
    List<Order> getOrdersByTable(Long tableId);
    List<Order> getKitchenOrders(String status);
    Order updateKitchenStatus(Long orderId, Long itemId, String status);
    Order cancelOrder(Long id);
    List<Order> getTodayOrders();
    List<Order> getActiveOrders();
    Optional<Order> getOrderWithDetails(Long id);
    Long getOrderCountByStatus(OrderStatus status);
    List<Order> getOrdersByDateRange(LocalDateTime start, LocalDateTime end);
    List<Order> getOrdersByUser(Long userId);
    Order createOrderFromEntity(Order order);
    List<Order> getOrdersByStatusNotIn(List<OrderStatus> excludedStatuses);
}