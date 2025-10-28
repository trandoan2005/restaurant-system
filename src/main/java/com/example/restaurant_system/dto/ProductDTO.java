package com.example.restaurant_system.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private Long id;               // Mã sản phẩm
    private String title;          // Tên sản phẩm
    private String description;    // Mô tả sản phẩm
    private Double price;          // Giá tiền
    private String photo;          // Đường dẫn ảnh
    private Integer stockQuantity; // Số lượng tồn kho
    private Long categoryId;       // Mã danh mục (dùng để lọc ở frontend)

    private String category;       // ✅ Tên danh mục (dùng để hiển thị ở backend/frontend)
}
