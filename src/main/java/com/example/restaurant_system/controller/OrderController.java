package com.example.restaurant_system.controller;

import com.example.restaurant_system.Service.OrderService;
import com.example.restaurant_system.dto.OrderRequest;
import com.example.restaurant_system.dto.OrderItemRequest;
import com.example.restaurant_system.entity.Order;
import com.example.restaurant_system.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ✅ GET ALL ORDERS (sắp xếp mới nhất)
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            System.out.println("📋 Getting all orders...");
            List<Order> orders = orderService.getAllOrders();
            System.out.println("✅ Found " + orders.size() + " orders");
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            System.out.println("❌ Error getting orders: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ GET ORDER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        try {
            System.out.println("🔍 Getting order by ID: " + id);
            return orderService.getOrderById(id)
                    .map(order -> {
                        System.out.println("✅ Order found: " + order.getId());
                        return ResponseEntity.ok(order);
                    })
                    .orElseGet(() -> {
                        System.out.println("❌ Order not found: " + id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            System.out.println("❌ Error getting order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ GET ORDER WITH DETAILS (eager loading)
    @GetMapping("/{id}/details")
    public ResponseEntity<Order> getOrderWithDetails(@PathVariable Long id) {
        try {
            System.out.println("🔍 Getting order details: " + id);
            return orderService.getOrderWithDetails(id)
                    .map(order -> {
                        System.out.println("✅ Order details found, items: " + order.getOrderDetails().size());
                        return ResponseEntity.ok(order);
                    })
                    .orElseGet(() -> {
                        System.out.println("❌ Order not found: " + id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            System.out.println("❌ Error getting order details: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ CREATE NEW ORDER - ĐÃ BỔ SUNG DEBUG CHI TIẾT
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        System.out.println("🎯 ========== ORDER CONTROLLER START ==========");
        
        try {
            // 🎯 DEBUG CHI TIẾT REQUEST DATA
            System.out.println("📦 [1] Received OrderRequest from Frontend:");
            System.out.println("   📋 TableId: " + orderRequest.getTableId());
            System.out.println("   📝 CustomerNote: " + orderRequest.getCustomerNote());
            System.out.println("   💰 TotalAmount: " + orderRequest.getTotalAmount());
            System.out.println("   📦 Items count: " + (orderRequest.getItems() != null ? orderRequest.getItems().size() : "NULL"));
            
            // 🎯 DEBUG CHI TIẾT TỪNG ITEM
            if (orderRequest.getItems() != null && !orderRequest.getItems().isEmpty()) {
                System.out.println("   🍽️ ITEMS DETAILS:");
                for (int i = 0; i < orderRequest.getItems().size(); i++) {
                    OrderItemRequest item = orderRequest.getItems().get(i);
                    System.out.println("     " + (i + 1) + ". " + item.getProductName() + 
                                     " (ID: " + item.getProductId() + ")" +
                                     " x" + item.getQuantity() + 
                                     " - " + item.getPrice() + "đ" +
                                     " - Note: '" + item.getNote() + "'");
                }
            } else {
                System.out.println("   ❌ NO ITEMS IN REQUEST!");
            }
            
            // 🎯 VALIDATE REQUEST DATA
            if (orderRequest.getTableId() == null) {
                System.out.println("❌ VALIDATION FAILED: tableId is null");
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Table ID không được để trống",
                    "timestamp", LocalDateTime.now()
                ));
            }
            
            if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
                System.out.println("❌ VALIDATION FAILED: items is empty");
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Order phải có ít nhất 1 món",
                    "timestamp", LocalDateTime.now()
                ));
            }
            
            System.out.println("✅ [2] Validation passed, calling OrderService...");
            
            // 🎯 GỌI SERVICE
            Order createdOrder = orderService.createOrder(orderRequest);
            
            System.out.println("✅ [3] Order created successfully!");
            System.out.println("   🆔 Order ID: " + createdOrder.getId());
            System.out.println("   📋 Order Status: " + createdOrder.getStatus());
            System.out.println("   💰 Final Total: " + createdOrder.getTotalAmount());
            System.out.println("🎯 ========== ORDER CONTROLLER SUCCESS ==========");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
            
        } catch (RuntimeException e) {
            System.out.println("❌ [RUNTIME ERROR] " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "timestamp", LocalDateTime.now(),
                "type", "RUNTIME_ERROR"
            ));
        } catch (Exception e) {
            System.out.println("❌ [UNEXPECTED ERROR] " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Lỗi server khi tạo order: " + e.getMessage(),
                        "timestamp", LocalDateTime.now(),
                        "type", "SERVER_ERROR"
                    ));
        }
    }

    // ✅ UPDATE ORDER
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody Order updatedOrder) {
        try {
            System.out.println("🔄 Updating order: " + id);
            Order order = orderService.updateOrder(id, updatedOrder);
            System.out.println("✅ Order updated: " + order.getId());
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            System.out.println("❌ " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ UPDATE ORDER STATUS - ĐÃ SỬA ĐỂ NHẬN REQUEST BODY
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            System.out.println("🔄 Updating order status: Order " + id);
            
            String statusStr = request.get("status");
            System.out.println("   📋 Requested status: " + statusStr);
            
            if (statusStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status không được để trống"));
            }
            
            OrderStatus status = OrderStatus.valueOf(statusStr.toUpperCase());
            Order order = orderService.updateOrderStatus(id, status);
            
            System.out.println("✅ Order status updated: " + order.getStatus());
            return ResponseEntity.ok(order);
            
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Invalid status value");
            return ResponseEntity.badRequest().body(Map.of("error", "Status không hợp lệ: " + e.getMessage()));
        } catch (RuntimeException e) {
            System.out.println("❌ " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ GET ORDERS BY STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable OrderStatus status) {
        try {
            System.out.println("📋 Getting orders by status: " + status);
            List<Order> orders = orderService.getOrdersByStatus(status);
            System.out.println("✅ Found " + orders.size() + " orders with status: " + status);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            System.out.println("❌ Error getting orders by status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ GET ORDERS BY TABLE
    @GetMapping("/table/{tableId}")
    public ResponseEntity<List<Order>> getOrdersByTable(@PathVariable Long tableId) {
        try {
            System.out.println("📋 Getting orders by table: " + tableId);
            List<Order> orders = orderService.getOrdersByTable(tableId);
            System.out.println("✅ Found " + orders.size() + " orders for table: " + tableId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            System.out.println("❌ Error getting orders by table: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ GET KITCHEN ORDERS
    @GetMapping("/kitchen")
    public ResponseEntity<List<Order>> getKitchenOrders(@RequestParam(required = false) String status) {
        try {
            System.out.println("👨‍🍳 Getting kitchen orders, status: " + status);
            List<Order> orders = orderService.getKitchenOrders(status);
            System.out.println("✅ Found " + orders.size() + " kitchen orders");
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            System.out.println("❌ " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ UPDATE KITCHEN STATUS
    @PutMapping("/{orderId}/items/{itemId}/kitchen-status")
    public ResponseEntity<?> updateKitchenStatus(@PathVariable Long orderId, 
                                               @PathVariable Long itemId,
                                               @RequestParam String status) {
        try {
            System.out.println("👨‍🍳 Updating kitchen status - Order: " + orderId + ", Item: " + itemId + ", Status: " + status);
            Order order = orderService.updateKitchenStatus(orderId, itemId, status);
            System.out.println("✅ Kitchen status updated");
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            System.out.println("❌ " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ CANCEL ORDER
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            System.out.println("❌ Cancelling order: " + id);
            Order order = orderService.cancelOrder(id);
            System.out.println("✅ Order cancelled: " + order.getId());
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            System.out.println("❌ " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ DELETE ORDER
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        try {
            System.out.println("🗑️ Deleting order: " + id);
            orderService.deleteOrder(id);
            System.out.println("✅ Order deleted: " + id);
            return ResponseEntity.ok().body(Map.of("message", "Order đã được xóa thành công"));
        } catch (RuntimeException e) {
            System.out.println("❌ " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ THÊM: GET TODAY'S ORDERS
    @GetMapping("/today")
    public ResponseEntity<List<Order>> getTodayOrders() {
        try {
            System.out.println("📅 Getting today's orders");
            List<Order> orders = orderService.getTodayOrders();
            System.out.println("✅ Found " + orders.size() + " orders today");
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            System.out.println("❌ Error getting today's orders: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ THÊM: GET ACTIVE ORDERS
    @GetMapping("/active")
    public ResponseEntity<List<Order>> getActiveOrders() {
        try {
            System.out.println("📋 Getting active orders");
            List<Order> orders = orderService.getActiveOrders();
            System.out.println("✅ Found " + orders.size() + " active orders");
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            System.out.println("❌ Error getting active orders: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ THÊM: GET ORDER COUNT BY STATUS
    @GetMapping("/count/{status}")
    public ResponseEntity<Long> getOrderCountByStatus(@PathVariable OrderStatus status) {
        try {
            System.out.println("🔢 Getting order count for status: " + status);
            Long count = orderService.getOrderCountByStatus(status);
            System.out.println("✅ Count: " + count + " orders with status: " + status);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            System.out.println("❌ Error getting order count: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ THÊM: HEALTH CHECK
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        System.out.println("🏥 Health check - Order API is running");
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "service", "Order Service", 
            "timestamp", LocalDateTime.now().toString(),
            "message", "Order API đang chạy tốt"
        ));
    }

    // ✅ THÊM: GET ORDERS BY DATE RANGE
    @GetMapping("/date-range")
    public ResponseEntity<List<Order>> getOrdersByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            System.out.println("📅 Getting orders from " + startDate + " to " + endDate);
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<Order> orders = orderService.getOrdersByDateRange(start, end);
            System.out.println("✅ Found " + orders.size() + " orders in date range");
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            System.out.println("❌ Error getting orders by date range: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }
}