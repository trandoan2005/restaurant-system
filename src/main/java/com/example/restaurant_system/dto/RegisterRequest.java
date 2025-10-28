package com.example.restaurant_system.dto;

import com.example.restaurant_system.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String phone;
    private Role role = Role.CUSTOMER; // ✅ SỬA: Mặc định là CUSTOMER thay vì EMPLOYEE
}