package com.example.restaurant_system.Service;

import com.example.restaurant_system.entity.Promotion;
import com.example.restaurant_system.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    public Optional<Promotion> getPromotionById(Long id) {
        return promotionRepository.findById(id);
    }

    public Promotion createPromotion(Promotion promotion) {
        // ✅ Validate dates
        if (promotion.getStartDate() != null && promotion.getEndDate() != null) {
            if (promotion.getEndDate().isBefore(promotion.getStartDate())) {
                throw new RuntimeException("❌ Ngày kết thúc phải sau ngày bắt đầu");
            }
        }

        // ✅ Validate discount values
        if (promotion.getDiscountAmount() == null && promotion.getDiscountPercentage() == null) {
            throw new RuntimeException("❌ Phải có ít nhất một loại giảm giá (số tiền hoặc phần trăm)");
        }

        promotion.setIsActive(true);
        return promotionRepository.save(promotion);
    }

    public Promotion updatePromotion(Long id, Promotion promotionDetails) {
        return promotionRepository.findById(id)
                .map(promotion -> {
                    promotion.setName(promotionDetails.getName());
                    promotion.setDiscountAmount(promotionDetails.getDiscountAmount());
                    promotion.setDiscountPercentage(promotionDetails.getDiscountPercentage());
                    promotion.setStartDate(promotionDetails.getStartDate());
                    promotion.setEndDate(promotionDetails.getEndDate());
                    promotion.setIsActive(promotionDetails.getIsActive());
                    
                    // ✅ Validate dates khi update
                    if (promotion.getStartDate() != null && promotion.getEndDate() != null) {
                        if (promotion.getEndDate().isBefore(promotion.getStartDate())) {
                            throw new RuntimeException("❌ Ngày kết thúc phải sau ngày bắt đầu");
                        }
                    }
                    
                    return promotionRepository.save(promotion);
                })
                .orElseThrow(() -> new RuntimeException("❌ Khuyến mãi không tồn tại với ID: " + id));
    }

    public void deletePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Khuyến mãi không tồn tại"));
        promotion.setIsActive(false); // ✅ Soft delete
        promotionRepository.save(promotion);
    }

    // ✅ THÊM: Lấy khuyến mãi đang active
    public List<Promotion> getActivePromotions() {
        return promotionRepository.findByIsActiveTrue();
    }

    // ✅ THÊM: Lấy khuyến mãi đang diễn ra
    public List<Promotion> getCurrentPromotions() {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findByIsActiveTrueAndStartDateBeforeAndEndDateAfter(now, now);
    }

    // ✅ THÊM: Tìm khuyến mãi theo tên
    public List<Promotion> searchPromotions(String name) {
        return promotionRepository.findByNameContainingIgnoreCase(name);
    }

    // ✅ THÊM: Tính toán giá sau khi áp dụng khuyến mãi
    public Double applyPromotion(Double originalPrice, Promotion promotion) {
        if (promotion == null || !promotion.getIsActive()) {
            return originalPrice;
        }

        // ✅ Kiểm tra thời gian hiệu lực
        LocalDateTime now = LocalDateTime.now();
        if (promotion.getStartDate() != null && promotion.getStartDate().isAfter(now)) {
            return originalPrice; // Chưa đến thời gian áp dụng
        }
        if (promotion.getEndDate() != null && promotion.getEndDate().isBefore(now)) {
            return originalPrice; // Đã hết hạn
        }

        Double finalPrice = originalPrice;

        // ✅ Áp dụng giảm giá theo số tiền
        if (promotion.getDiscountAmount() != null) {
            finalPrice -= promotion.getDiscountAmount();
        }

        // ✅ Áp dụng giảm giá theo phần trăm
        if (promotion.getDiscountPercentage() != null) {
            finalPrice -= originalPrice * (promotion.getDiscountPercentage() / 100);
        }

        // ✅ Đảm bảo giá không âm
        return Math.max(finalPrice, 0);
    }

    // ✅ THÊM: Kích hoạt/ngừng kích hoạt khuyến mãi
    public Promotion togglePromotionStatus(Long id, Boolean isActive) {
        return promotionRepository.findById(id)
                .map(promotion -> {
                    promotion.setIsActive(isActive);
                    return promotionRepository.save(promotion);
                })
                .orElseThrow(() -> new RuntimeException("❌ Khuyến mãi không tồn tại"));
    }
}