package com.example.restaurant_system.repository;

import com.example.restaurant_system.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    // ✅ THÊM: Tìm khuyến mãi đang active
    List<Promotion> findByIsActiveTrue();
    
    // ✅ THÊM: Tìm khuyến mãi đang diễn ra
    List<Promotion> findByIsActiveTrueAndStartDateBeforeAndEndDateAfter(
        LocalDateTime now, LocalDateTime now2);
    
    // ✅ THÊM: Tìm khuyến mãi theo tên
    List<Promotion> findByNameContainingIgnoreCase(String name);
}