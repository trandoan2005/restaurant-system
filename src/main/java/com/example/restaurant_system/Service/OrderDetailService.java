package com.example.restaurant_system.Service;

import com.example.restaurant_system.entity.OrderDetail;
import com.example.restaurant_system.entity.Product;
import com.example.restaurant_system.repository.OrderDetailRepository;
import com.example.restaurant_system.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderDetailService {
    
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;

    public List<OrderDetail> getAll() {
        return orderDetailRepository.findAll();
    }

    public Optional<OrderDetail> getById(Long id) {
        return orderDetailRepository.findById(id);
    }

    @Transactional
    public OrderDetail save(OrderDetail orderDetail) {
        // ✅ Validate product (nếu có product entity)
        if (orderDetail.getProductId() == null) {
            throw new IllegalArgumentException("Product ID không được để trống");
        }
        
        // ✅ Kiểm tra product có tồn tại không (nếu cần validate)
        try {
            Product product = productRepository.findById(orderDetail.getProductId()).orElse(null);
            if (product != null) {
                // Kiểm tra số lượng tồn kho nếu có
                if (product.getStockQuantity() != null && product.getStockQuantity() < orderDetail.getQuantity()) {
                    throw new RuntimeException("Sản phẩm " + product.getTitle() + " không đủ số lượng. Tồn kho: " + product.getStockQuantity());
                }
                
                // Trừ số lượng tồn kho nếu có
                if (product.getStockQuantity() != null) {
                    product.setStockQuantity(product.getStockQuantity() - orderDetail.getQuantity());
                    productRepository.save(product);
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Không thể validate product: " + e.getMessage());
        }
        
        return orderDetailRepository.save(orderDetail);
    }

    public void delete(Long id) {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderDetail không tồn tại"));
        
        // ✅ Restore stock quantity khi xóa order detail
        restoreProductStock(orderDetail);
        
        orderDetailRepository.deleteById(id);
    }

    // ✅ Lấy tất cả order details theo order ID
    public List<OrderDetail> getByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    // ✅ Lấy tất cả order details theo product ID
    public List<OrderDetail> getByProductId(Long productId) {
        return orderDetailRepository.findByProductId(productId);
    }

    // ✅ SỬA LỖI: Tính tổng số lượng đã bán của sản phẩm
    public Long getTotalSoldQuantityByProduct(Long productId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByProductId(productId);
        return orderDetails.stream()
                .mapToLong(OrderDetail::getQuantity)
                .sum();
    }

    // ✅ Cập nhật số lượng order detail
    @Transactional
    public OrderDetail updateQuantity(Long id, Integer newQuantity) {
        return orderDetailRepository.findById(id)
                .map(orderDetail -> {
                    int oldQuantity = orderDetail.getQuantity();
                    
                    // Restore stock cũ trước
                    restoreProductStock(orderDetail);
                    
                    // Kiểm tra stock mới
                    try {
                        Product product = productRepository.findById(orderDetail.getProductId()).orElse(null);
                        if (product != null && product.getStockQuantity() != null) {
                            if (product.getStockQuantity() < newQuantity) {
                                throw new RuntimeException("Không đủ số lượng tồn kho");
                            }
                            
                            // Cập nhật số lượng mới và trừ stock
                            orderDetail.setQuantity(newQuantity);
                            product.setStockQuantity(product.getStockQuantity() - newQuantity);
                            productRepository.save(product);
                        }
                    } catch (Exception e) {
                        System.out.println("⚠️ Không thể update quantity: " + e.getMessage());
                    }
                    
                    return orderDetailRepository.save(orderDetail);
                })
                .orElseThrow(() -> new RuntimeException("OrderDetail không tồn tại"));
    }

    // ✅ Cập nhật trạng thái kitchen
    @Transactional
    public OrderDetail updateKitchenStatus(Long id, String kitchenStatus) {
        return orderDetailRepository.findById(id)
                .map(orderDetail -> {
                    orderDetail.setKitchenStatus(kitchenStatus);
                    return orderDetailRepository.save(orderDetail);
                })
                .orElseThrow(() -> new RuntimeException("OrderDetail không tồn tại"));
    }

    // ✅ Helper method để restore stock
    private void restoreProductStock(OrderDetail orderDetail) {
        try {
            Product product = productRepository.findById(orderDetail.getProductId()).orElse(null);
            if (product != null && product.getStockQuantity() != null) {
                product.setStockQuantity(product.getStockQuantity() + orderDetail.getQuantity());
                productRepository.save(product);
            }
        } catch (Exception e) {
            System.out.println("⚠️ Không thể restore product stock: " + e.getMessage());
        }
    }

    // ✅ Tính tổng tiền của order details theo order
    public Double calculateOrderTotal(Long orderId) {
        List<OrderDetail> orderDetails = getByOrderId(orderId);
        return orderDetails.stream()
                .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                .sum();
    }

    // ✅ Lấy order details theo trạng thái kitchen
    public List<OrderDetail> getByKitchenStatus(String kitchenStatus) {
        return orderDetailRepository.findByKitchenStatus(kitchenStatus);
    }

    // ✅ Đếm số order details theo trạng thái
    public Long countByKitchenStatus(String kitchenStatus) {
        return orderDetailRepository.countByKitchenStatus(kitchenStatus);
    }
}