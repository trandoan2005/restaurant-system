package com.example.restaurant_system.controller;

import com.example.restaurant_system.Service.ProductService;
import com.example.restaurant_system.dto.ApiResponse;
import com.example.restaurant_system.dto.ProductDTO;
import com.example.restaurant_system.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"})
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts() {
        try {
            List<ProductDTO> dtos = productService.getAllProducts();
            return ResponseEntity.ok(ApiResponse.success("✅ Lấy danh sách sản phẩm thành công", dtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id) {
        try {
            return productService.getProductById(id)
                .map(p -> {
                    ProductDTO dto = new ProductDTO();
                    dto.setId(p.getId());
                    dto.setTitle(p.getTitle());
                    dto.setDescription(p.getDescription());
                    dto.setPrice(p.getPrice());
                    dto.setPhoto(p.getPhoto());
                    dto.setStockQuantity(p.getStockQuantity());
                    dto.setCategory(p.getCategory() != null ? p.getCategory().getTitle() : null);
                    return ResponseEntity.ok(ApiResponse.success("✅ Lấy sản phẩm thành công", dto));
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createProduct(@RequestBody Product product) {
        try {
            Product savedProduct = productService.createProduct(product);
            ProductDTO dto = new ProductDTO();
            dto.setId(savedProduct.getId());
            dto.setTitle(savedProduct.getTitle());
            dto.setDescription(savedProduct.getDescription());
            dto.setPrice(savedProduct.getPrice());
            dto.setPhoto(savedProduct.getPhoto());
            dto.setStockQuantity(savedProduct.getStockQuantity());
            dto.setCategory(savedProduct.getCategory() != null ? savedProduct.getCategory().getTitle() : null);

            return ResponseEntity.status(201).body(ApiResponse.success("✅ Tạo sản phẩm thành công", dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi tạo sản phẩm: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            Product updated = productService.updateProduct(id, product);
            ProductDTO dto = new ProductDTO();
            dto.setId(updated.getId());
            dto.setTitle(updated.getTitle());
            dto.setDescription(updated.getDescription());
            dto.setPrice(updated.getPrice());
            dto.setPhoto(updated.getPhoto());
            dto.setStockQuantity(updated.getStockQuantity());
            dto.setCategory(updated.getCategory() != null ? updated.getCategory().getTitle() : null);

            return ResponseEntity.ok(ApiResponse.success("✅ Cập nhật sản phẩm thành công", dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi cập nhật: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(ApiResponse.success("✅ Xóa sản phẩm thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi xóa: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchProducts(@RequestParam String keyword) {
        try {
            List<ProductDTO> dtos = productService.searchProducts(keyword).stream()
                .map(p -> {
                    ProductDTO dto = new ProductDTO();
                    dto.setId(p.getId());
                    dto.setTitle(p.getTitle());
                    dto.setDescription(p.getDescription());
                    dto.setPrice(p.getPrice());
                    dto.setPhoto(p.getPhoto());
                    dto.setStockQuantity(p.getStockQuantity());
                    dto.setCategory(p.getCategory() != null ? p.getCategory().getTitle() : null);
                    return dto;
                })
                .toList();

            return ResponseEntity.ok(ApiResponse.success("✅ Tìm kiếm thành công", dtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi tìm kiếm: " + e.getMessage()));
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse> getProductsByCategory(@PathVariable Long categoryId) {
        try {
            List<ProductDTO> dtos = productService.getProductsByCategory(categoryId).stream()
                .map(p -> {
                    ProductDTO dto = new ProductDTO();
                    dto.setId(p.getId());
                    dto.setTitle(p.getTitle());
                    dto.setDescription(p.getDescription());
                    dto.setPrice(p.getPrice());
                    dto.setPhoto(p.getPhoto());
                    dto.setStockQuantity(p.getStockQuantity());
                    dto.setCategory(p.getCategory() != null ? p.getCategory().getTitle() : null);
                    return dto;
                })
                .toList();

            return ResponseEntity.ok(ApiResponse.success("✅ Lấy sản phẩm theo danh mục thành công", dtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi: " + e.getMessage()));
        }
    }
}
