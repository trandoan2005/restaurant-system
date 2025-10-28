package com.example.restaurant_system.Service;

import com.example.restaurant_system.dto.BillRequest;
import com.example.restaurant_system.entity.Bill;
import com.example.restaurant_system.entity.Order;
import com.example.restaurant_system.entity.Promotion;
import com.example.restaurant_system.repository.BillRepository;
import com.example.restaurant_system.repository.OrderRepository;
import com.example.restaurant_system.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final OrderRepository orderRepository;
    private final PromotionRepository promotionRepository;

    public Bill createBill(BillRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("❌ Đơn hàng không tồn tại"));

        double total = calculateTotal(order);

        // ✅ Áp dụng khuyến mãi nếu có
        if (request.getPromotionId() != null) {
            Promotion promo = promotionRepository.findById(request.getPromotionId())
                    .orElseThrow(() -> new RuntimeException("❌ Khuyến mãi không tồn tại"));

            if (promo.getEndDate() != null && promo.getEndDate().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("❌ Khuyến mãi đã hết hạn");
            }

            if (promo.getDiscountAmount() != null) {
                total -= promo.getDiscountAmount();
            } else if (promo.getDiscountPercentage() != null) {
                total -= total * (promo.getDiscountPercentage() / 100);
            }
        }

        Bill bill = new Bill();
        bill.setBillNumber(generateBillNumber()); // ✅ Tạo số hóa đơn
        bill.setOrder(order);
        bill.setTotalAmount(total);
        bill.setCreatedAt(LocalDateTime.now());

        return billRepository.save(bill);
    }

    private double calculateTotal(Order order) {
        return order.getOrderDetails().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    // ✅ Tạo số hóa đơn tự động
    private String generateBillNumber() {
        return "BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public List<Bill> getAll() {
        return billRepository.findAll();
    }

    public Optional<Bill> getById(Long id) {
        return billRepository.findById(id);
    }

    // ✅ THÊM: Tìm hóa đơn theo số hóa đơn
    public Optional<Bill> getByBillNumber(String billNumber) {
        return billRepository.findByBillNumber(billNumber);
    }

    public void delete(Long id) {
        billRepository.deleteById(id);
    }
}