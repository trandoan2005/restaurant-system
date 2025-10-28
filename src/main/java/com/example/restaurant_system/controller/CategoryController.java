package com.example.restaurant_system.controller;

import com.example.restaurant_system.Service.CategoryService;
import com.example.restaurant_system.dto.ApiResponse;
import com.example.restaurant_system.dto.CategoryDTO;
import com.example.restaurant_system.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllCategories() {
        try {
            List<CategoryDTO> dtos = categoryService.getAllCategories().stream()
                .map(cat -> {
                    CategoryDTO dto = new CategoryDTO();
                    dto.setId(cat.getId());
                    dto.setTitle(cat.getTitle());
                    dto.setDescription(cat.getDescription());
                    dto.setPhoto(cat.getPhoto());
                    return dto;
                })
                .toList();

            return ResponseEntity.ok(ApiResponse.success("✅ Lấy danh sách danh mục thành công", dtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long id) {
        try {
            return categoryService.getCategoryById(id)
                    .map(category -> {
                        CategoryDTO dto = new CategoryDTO();
                        dto.setId(category.getId());
                        dto.setTitle(category.getTitle());
                        dto.setDescription(category.getDescription());
                        dto.setPhoto(category.getPhoto());
                        return ResponseEntity.ok(ApiResponse.success("✅ Lấy danh mục thành công", dto));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createCategory(@RequestBody Category category) {
        try {
            Category saved = categoryService.saveCategory(category);
            CategoryDTO dto = new CategoryDTO();
            dto.setId(saved.getId());
            dto.setTitle(saved.getTitle());
            dto.setDescription(saved.getDescription());
            dto.setPhoto(saved.getPhoto());

            return ResponseEntity.status(201).body(ApiResponse.success("✅ Tạo danh mục thành công", dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi tạo danh mục: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            Category existing = categoryService.getCategoryById(id)
                    .orElseThrow(() -> new RuntimeException("❌ Danh mục không tồn tại"));

            existing.setTitle(category.getTitle());
            existing.setDescription(category.getDescription());
            existing.setPhoto(category.getPhoto());

            Category saved = categoryService.saveCategory(existing);
            CategoryDTO dto = new CategoryDTO();
            dto.setId(saved.getId());
            dto.setTitle(saved.getTitle());
            dto.setDescription(saved.getDescription());
            dto.setPhoto(saved.getPhoto());

            return ResponseEntity.ok(ApiResponse.success("✅ Cập nhật danh mục thành công", dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi cập nhật: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(ApiResponse.success("✅ Xóa danh mục thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi xóa: " + e.getMessage()));
        }
    }
}
