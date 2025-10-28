package com.example.restaurant_system.enums;

public enum ReservationStatus {
    PENDING,      // Khách vừa đặt, chờ xác nhận
    CONFIRMED,    // Đặt bàn đã được xác nhận
    CANCELLED,    // Đặt bàn bị hủy
    COMPLETED     // Khách đã đến và dùng xong
}
