package com.example.restaurant_system.repository;

import com.example.restaurant_system.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // ✅ Kiểm tra category tồn tại theo tên
    boolean existsByTitle(String title);

    // ✅ Tìm category theo tên
    Optional<Category> findByTitle(String title);
}