package com.example.restaurant_system.dto;

import com.example.restaurant_system.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private Role role; // ✅ QUAN TRỌNG: Thêm field role
    private List<String> roles;

    // ✅ Chỉ GIỮ LẠI 1 CONSTRUCTOR đầy đủ
    public JwtResponse(String token, Long id, String username, String email, 
                      String fullName, String phone, Role role, List<String> roles) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role; // ✅ QUAN TRỌNG
        this.roles = roles;
    }
}