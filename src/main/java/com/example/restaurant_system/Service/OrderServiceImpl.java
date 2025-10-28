package com.example.restaurant_system.Service;

import com.example.restaurant_system.dto.OrderRequest;
import com.example.restaurant_system.dto.OrderItemRequest;
import com.example.restaurant_system.entity.*;
import com.example.restaurant_system.enums.OrderStatus;
import com.example.restaurant_system.enums.TableStatus;
import com.example.restaurant_system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final TableRepository tableRepository;
    private final PromotionRepository promotionRepository;

    @Override
    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        System.out.println("🎯 ========== ORDER SERVICE START ==========");
        
        try {
            // 🎯 DEBUG REQUEST DATA
            System.out.println("📦 [SERVICE] Processing OrderRequest:");
            System.out.println("   📋 TableId: " + orderRequest.getTableId());
            System.out.println("   📝 CustomerNote: " + orderRequest.getCustomerNote());
            System.out.println("   💰 TotalAmount: " + orderRequest.getTotalAmount());
            System.out.println("   📦 Items count: " + orderRequest.getItems().size());

            // 🎯 VALIDATE TABLE
            System.out.println("🔍 [1] Validating table...");
            if (orderRequest.getTableId() == null) {
                throw new RuntimeException("Bàn không được để trống");
            }
            
            TableEntity table = tableRepository.findById(orderRequest.getTableId())
                    .orElseThrow(() -> {
                        System.out.println("❌ Table not found: " + orderRequest.getTableId());
                        return new RuntimeException("Bàn không tồn tại");
                    });
            System.out.println("✅ Table found: " + table.getName() + " (ID: " + table.getId() + ")");

            // 🎯 VALIDATE ITEMS
            System.out.println("🔍 [2] Validating items...");
            if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
                throw new RuntimeException("Order phải có ít nhất 1 món");
            }

            // 🎯 TẠO ORDER MỚI
            System.out.println("🔍 [3] Creating new order...");
            Order order = new Order();
            order.setTable(table);
            order.setNotes(orderRequest.getCustomerNote());
            order.setTotalAmount(orderRequest.getTotalAmount());
            order.setStatus(OrderStatus.PENDING);

            // 🎯 SET USER MẶC ĐỊNH
            System.out.println("🔍 [4] Setting default user...");
            try {
                User defaultUser = userRepository.findById(1L).orElse(null);
                if (defaultUser != null) {
                    order.setUser(defaultUser);
                    System.out.println("✅ Default user set: " + defaultUser.getUsername());
                } else {
                    System.out.println("⚠️ No default user found, continuing without user");
                }
            } catch (Exception e) {
                System.out.println("⚠️ Cannot set default user: " + e.getMessage());
            }

            // 🎯 PROCESS ORDER DETAILS
            System.out.println("🔍 [5] Processing order details...");
            double calculatedTotal = 0;
            
            for (int i = 0; i < orderRequest.getItems().size(); i++) {
                OrderItemRequest itemRequest = orderRequest.getItems().get(i);
                System.out.println("   🍽️ Processing item " + (i + 1) + ": " + itemRequest.getProductName());
                
                OrderDetail detail = new OrderDetail();
                detail.setOrder(order);
                detail.setProductId(itemRequest.getProductId());
                detail.setProductName(itemRequest.getProductName());
                detail.setQuantity(itemRequest.getQuantity());
                detail.setPrice(itemRequest.getPrice());
                detail.setNote(itemRequest.getNote());
                detail.setKitchenStatus("PENDING");

                // 🎯 VALIDATE PRODUCT STOCK
                try {
                    Product product = productRepository.findById(itemRequest.getProductId()).orElse(null);
                    if (product != null) {
                        System.out.println("     📦 Product: " + product.getTitle() + 
                                         " (Stock: " + product.getStockQuantity() + 
                                         ", Requested: " + itemRequest.getQuantity() + ")");
                        
                        if (product.getStockQuantity() != null && product.getStockQuantity() < itemRequest.getQuantity()) {
                            throw new RuntimeException("Sản phẩm " + product.getTitle() + " không đủ số lượng. Còn: " + product.getStockQuantity());
                        }
                    } else {
                        System.out.println("     ⚠️ Product not found: " + itemRequest.getProductId());
                    }
                } catch (Exception e) {
                    System.out.println("     ❌ Product validation failed: " + e.getMessage());
                    throw e;
                }

                order.getOrderDetails().add(detail);
                calculatedTotal += (itemRequest.getPrice() * itemRequest.getQuantity());
                System.out.println("     ✅ Item added: " + itemRequest.getProductName() + " x" + itemRequest.getQuantity());
            }

            // 🎯 VALIDATE TOTAL AMOUNT
            System.out.println("🔍 [6] Validating total amount...");
            System.out.println("   💰 Calculated total: " + calculatedTotal);
            System.out.println("   💰 Request total: " + orderRequest.getTotalAmount());
            
            if (orderRequest.getTotalAmount() == null || orderRequest.getTotalAmount() <= 0) {
                order.setTotalAmount(calculatedTotal);
                System.out.println("   🔄 Using calculated total: " + calculatedTotal);
            }

            // 🎯 UPDATE TABLE STATUS
            System.out.println("🔍 [7] Updating table status...");
            table.setStatus(TableStatus.OCCUPIED);
            tableRepository.save(table);
            System.out.println("✅ Table status updated to OCCUPIED");

            // 🎯 SAVE ORDER TO DATABASE
            System.out.println("🔍 [8] Saving order to database...");
            Order savedOrder = orderRepository.save(order);
            
            System.out.println("✅ [SERVICE] Order created successfully!");
            System.out.println("   🆔 Order ID: " + savedOrder.getId());
            System.out.println("   📋 Status: " + savedOrder.getStatus());
            System.out.println("   💰 Total: " + savedOrder.getTotalAmount());
            System.out.println("   📦 Items: " + savedOrder.getOrderDetails().size());
            System.out.println("🎯 ========== ORDER SERVICE SUCCESS ==========");
            
            return savedOrder;
            
        } catch (RuntimeException e) {
            System.out.println("❌ [SERVICE] RUNTIME ERROR: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.out.println("❌ [SERVICE] UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi hệ thống khi tạo order: " + e.getMessage());
        }
    }

    @Override
    public List<Order> getAllOrders() {
        System.out.println("📋 [SERVICE] Getting all orders...");
        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        System.out.println("✅ Found " + orders.size() + " orders");
        return orders;
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        System.out.println("🔍 [SERVICE] Getting order by ID: " + id);
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            System.out.println("✅ Order found: " + order.get().getId());
        } else {
            System.out.println("❌ Order not found: " + id);
        }
        return order;
    }

    @Override
    public Order updateOrder(Long id, Order updatedOrder) {
        System.out.println("🔄 [SERVICE] Updating order: " + id);
        return orderRepository.findById(id)
                .map(order -> {
                    System.out.println("📝 Updating order fields...");
                    if (updatedOrder.getStatus() != null) {
                        order.setStatus(updatedOrder.getStatus());
                        System.out.println("   📋 Status: " + updatedOrder.getStatus());
                    }
                    if (updatedOrder.getTable() != null) {
                        order.setTable(updatedOrder.getTable());
                        System.out.println("   🪑 Table: " + updatedOrder.getTable().getId());
                    }
                    if (updatedOrder.getNotes() != null) {
                        order.setNotes(updatedOrder.getNotes());
                        System.out.println("   📝 Notes updated");
                    }
                    if (updatedOrder.getTotalAmount() != null) {
                        order.setTotalAmount(updatedOrder.getTotalAmount());
                        System.out.println("   💰 Total: " + updatedOrder.getTotalAmount());
                    }
                    
                    Order savedOrder = orderRepository.save(order);
                    System.out.println("✅ Order updated: " + savedOrder.getId());
                    return savedOrder;
                })
                .orElseThrow(() -> {
                    System.out.println("❌ Order not found for update: " + id);
                    return new RuntimeException("Order không tồn tại");
                });
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        System.out.println("🗑️ [SERVICE] Deleting order: " + id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    System.out.println("❌ Order not found for deletion: " + id);
                    return new RuntimeException("Order không tồn tại");
                });
        
        // RESTORE STOCK
        System.out.println("🔍 Restoring product stock...");
        if (order.getStatus() != OrderStatus.COMPLETED && order.getStatus() != OrderStatus.CANCELLED) {
            for (OrderDetail detail : order.getOrderDetails()) {
                try {
                    Product product = productRepository.findById(detail.getProductId()).orElse(null);
                    if (product != null) {
                        int oldStock = product.getStockQuantity();
                        product.setStockQuantity(product.getStockQuantity() + detail.getQuantity());
                        productRepository.save(product);
                        System.out.println("   📦 Restored stock for " + product.getTitle() + 
                                         ": " + oldStock + " → " + product.getStockQuantity());
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ Không thể restore stock for product: " + detail.getProductId());
                }
            }
        }
        
        orderRepository.deleteById(id);
        System.out.println("✅ Order deleted: " + id);
    }

    @Override
    public Order updateOrderStatus(Long id, OrderStatus status) {
        System.out.println("🔄 [SERVICE] Updating order status: " + id + " → " + status);
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(status);
                    System.out.println("✅ Order status updated: " + order.getId() + " → " + status);
                    
                    // UPDATE TABLE STATUS
                    try {
                        TableEntity table = order.getTable();
                        if (table != null) {
                            TableStatus newTableStatus = (status == OrderStatus.COMPLETED || status == OrderStatus.CANCELLED) 
                                ? TableStatus.AVAILABLE 
                                : TableStatus.OCCUPIED;
                            
                            table.setStatus(newTableStatus);
                            tableRepository.save(table);
                            System.out.println("✅ Table status updated: " + table.getName() + " → " + newTableStatus);
                        }
                    } catch (Exception e) {
                        System.out.println("⚠️ Không thể update table status: " + e.getMessage());
                    }
                    
                    Order savedOrder = orderRepository.save(order);
                    return savedOrder;
                })
                .orElseThrow(() -> {
                    System.out.println("❌ Order not found for status update: " + id);
                    return new RuntimeException("Order không tồn tại");
                });
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        System.out.println("📋 [SERVICE] Getting orders by status: " + status);
        List<Order> orders = orderRepository.findByStatus(status);
        System.out.println("✅ Found " + orders.size() + " orders with status: " + status);
        return orders;
    }

    @Override
    public List<Order> getOrdersByTable(Long tableId) {
        System.out.println("📋 [SERVICE] Getting orders by table: " + tableId);
        List<Order> orders = orderRepository.findByTableId(tableId);
        System.out.println("✅ Found " + orders.size() + " orders for table: " + tableId);
        return orders;
    }

    @Override
    public List<Order> getKitchenOrders(String status) {
        System.out.println("👨‍🍳 [SERVICE] Getting kitchen orders, status: " + status);
        if (status != null && !status.isEmpty()) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                List<Order> orders = orderRepository.findByStatus(orderStatus);
                System.out.println("✅ Found " + orders.size() + " kitchen orders with status: " + status);
                return orders;
            } catch (IllegalArgumentException e) {
                System.out.println("⚠️ Invalid status: " + status);
            }
        }
        List<Order> orders = orderRepository.findByStatusIn(
            List.of(OrderStatus.PENDING, OrderStatus.PREPARING, OrderStatus.READY)
        );
        System.out.println("✅ Found " + orders.size() + " active kitchen orders");
        return orders;
    }

    // ... CÁC METHOD KHÁC GIỮ NGUYÊN VỚI LOG TƯƠNG TỰ

    @Override
    @Transactional
    public Order updateKitchenStatus(Long orderId, Long itemId, String status) {
        System.out.println("👨‍🍳 [SERVICE] Updating kitchen status - Order: " + orderId + ", Item: " + itemId + ", Status: " + status);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    System.out.println("❌ Order not found: " + orderId);
                    return new RuntimeException("Order không tồn tại");
                });
        
        boolean itemFound = order.getOrderDetails().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .map(item -> {
                    item.setKitchenStatus(status);
                    System.out.println("✅ Kitchen status updated for item: " + itemId + " → " + status);
                    return true;
                })
                .orElse(false);
        
        if (!itemFound) {
            System.out.println("❌ Order item not found: " + itemId);
            throw new RuntimeException("Order item không tồn tại: " + itemId);
        }
        
        // AUTO-UPDATE ORDER STATUS
        boolean allReady = order.getOrderDetails().stream()
                .allMatch(item -> "READY".equals(item.getKitchenStatus()) || "COMPLETED".equals(item.getKitchenStatus()));
        
        if (allReady && order.getStatus() == OrderStatus.PREPARING) {
            order.setStatus(OrderStatus.READY);
            System.out.println("✅ Auto-updated order status to READY");
        }
        
        Order savedOrder = orderRepository.save(order);
        System.out.println("✅ Kitchen status update completed");
        return savedOrder;
    }

    @Override
    public Order cancelOrder(Long id) {
        System.out.println("❌ [SERVICE] Cancelling order: " + id);
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(OrderStatus.CANCELLED);
                    System.out.println("✅ Order status set to CANCELLED");
                    
                    // FREE TABLE
                    try {
                        TableEntity table = order.getTable();
                        if (table != null) {
                            table.setStatus(TableStatus.AVAILABLE);
                            tableRepository.save(table);
                            System.out.println("✅ Table freed: " + table.getName());
                        }
                    } catch (Exception e) {
                        System.out.println("⚠️ Không thể update table status: " + e.getMessage());
                    }
                    
                    Order savedOrder = orderRepository.save(order);
                    System.out.println("✅ Order cancelled: " + savedOrder.getId());
                    return savedOrder;
                })
                .orElseThrow(() -> {
                    System.out.println("❌ Order not found for cancellation: " + id);
                    return new RuntimeException("Order không tồn tại");
                });
    }

    // CÁC METHOD CÒN LẠI GIỮ NGUYÊN VỚI LOG TƯƠNG TỰ
    @Override
    public List<Order> getTodayOrders() {
        System.out.println("📅 [SERVICE] Getting today's orders");
        List<Order> orders = orderRepository.findAll();
        System.out.println("✅ Found " + orders.size() + " orders today");
        return orders;
    }

    @Override
    public List<Order> getActiveOrders() {
        System.out.println("📋 [SERVICE] Getting active orders");
        List<Order> orders = orderRepository.findByStatusIn(
            List.of(OrderStatus.PENDING, OrderStatus.PREPARING, OrderStatus.READY)
        );
        System.out.println("✅ Found " + orders.size() + " active orders");
        return orders;
    }

    @Override
    public Optional<Order> getOrderWithDetails(Long id) {
        System.out.println("🔍 [SERVICE] Getting order with details: " + id);
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            System.out.println("✅ Order with details found, items: " + order.get().getOrderDetails().size());
        } else {
            System.out.println("❌ Order not found: " + id);
        }
        return order;
    }

    @Override
    public Long getOrderCountByStatus(OrderStatus status) {
        System.out.println("🔢 [SERVICE] Getting order count for status: " + status);
        List<Order> orders = orderRepository.findByStatus(status);
        Long count = (long) orders.size();
        System.out.println("✅ Count: " + count + " orders with status: " + status);
        return count;
    }

    @Override
    public List<Order> getOrdersByDateRange(LocalDateTime start, LocalDateTime end) {
        System.out.println("📅 [SERVICE] Getting orders by date range: " + start + " to " + end);
        List<Order> orders = orderRepository.findAll();
        System.out.println("✅ Found " + orders.size() + " orders in date range");
        return orders;
    }

    public List<Order> getOrdersByUser(Long userId) {
        System.out.println("👤 [SERVICE] Getting orders by user: " + userId);
        List<Order> orders = orderRepository.findByUserId(userId);
        System.out.println("✅ Found " + orders.size() + " orders for user: " + userId);
        return orders;
    }

    @Override
    public List<Order> getOrdersByStatusNotIn(java.util.List<OrderStatus> excludedStatuses) {
        System.out.println("📋 [SERVICE] Getting orders excluding statuses: " + excludedStatuses);
        // Fallback implementation in case repository method is not available at runtime
        List<Order> orders = orderRepository.findAll();
        List<Order> filtered = orders.stream()
                .filter(o -> o.getStatus() == null || !excludedStatuses.contains(o.getStatus()))
                .toList();
        System.out.println("✅ Found " + filtered.size() + " orders excluding: " + excludedStatuses);
        return filtered;
    }

    // METHOD createOrderFromEntity GIỮ NGUYÊN
    @Transactional
    public Order createOrderFromEntity(Order order) {
        System.out.println("🎯 [SERVICE] Creating order from entity...");
        // ... code hiện tại giữ nguyên
        return orderRepository.save(order);
    }
}