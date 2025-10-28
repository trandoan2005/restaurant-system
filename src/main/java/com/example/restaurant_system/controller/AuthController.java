package com.example.restaurant_system.controller;

import com.example.restaurant_system.dto.*;
import com.example.restaurant_system.entity.User;
import com.example.restaurant_system.enums.Role;
import com.example.restaurant_system.repository.UserRepository;
import com.example.restaurant_system.config.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
        try {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("❌ Username đã tồn tại"));
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("❌ Email đã tồn tại"));
            }

            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            user.setFullName(request.getFullName());
            user.setPhone(request.getPhone());
            
            // ✅ SỬA: CHO PHÉP ROLE TỪ REQUEST, mặc định là CUSTOMER
            Role userRole = (request.getRole() != null) ? request.getRole() : Role.CUSTOMER;
            user.setRole(userRole);
            user.setIsActive(true);

            userRepository.save(user);

            return ResponseEntity.status(201).body(ApiResponse.success("✅ Đăng ký thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi đăng ký: " + e.getMessage()));
        }
    }

    @PostMapping("/register-admin")
    public ResponseEntity<ApiResponse> registerAdmin(@RequestBody RegisterRequest request) {
        try {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("❌ Username đã tồn tại"));
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("❌ Email đã tồn tại"));
            }

            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            user.setFullName(request.getFullName());
            user.setPhone(request.getPhone());
            
            // ✅ API RIÊNG: LUÔN TẠO ADMIN
            user.setRole(Role.ADMIN);
            user.setIsActive(true);

            userRepository.save(user);

            return ResponseEntity.status(201).body(ApiResponse.success("✅ Đăng ký admin thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi đăng ký admin: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        try {
            System.out.println("=== DEBUG LOGIN START ===");
            System.out.println("Username: " + request.getUsername());
            
            // ✅ Thử authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            System.out.println("=== DEBUG: Authentication SUCCESS ===");
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtUtils.generateToken(authentication);

            User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
            
            List<String> roles = List.of(user.getRole().name());

            // ✅ SỬA: Tạo JwtResponse với đầy đủ thông tin
            JwtResponse response = new JwtResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRole(), // ✅ QUAN TRỌNG: Trả về role
                roles
            );

            System.out.println("=== DEBUG: Login COMPLETE ===");
            System.out.println("User role: " + user.getRole());
            System.out.println("Response data: " + response);
            return ResponseEntity.ok(ApiResponse.success("✅ Đăng nhập thành công", response));
        } catch (Exception e) {
            System.out.println("=== DEBUG: Login FAILED ===");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Sai username hoặc password"));
        }
    }
}