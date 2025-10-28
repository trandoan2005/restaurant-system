package com.example.restaurant_system.enums;

public enum TableStatus {
    AVAILABLE,      // Có sẵn (thay thế FREE)
    OCCUPIED,       // Đang có khách  
    RESERVED,       // Đã đặt trước
    MAINTENANCE,    // Đang bảo trì
    CLEANING,       // Đang dọn dẹp
    FREE            // ✅ Giữ lại để tương thích với dữ liệu cũ
}
