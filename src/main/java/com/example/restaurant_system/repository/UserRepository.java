package com.example.restaurant_system.repository;

import com.example.restaurant_system.entity.User;
import com.example.restaurant_system.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    
    // ✅ THÊM: Tìm user theo role
    List<User> findByRole(Role role);
    
    // ✅ THÊM: Tìm user đang active
    List<User> findByIsActiveTrue();
    
    // ✅ THÊM: Kiểm tra email tồn tại
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}