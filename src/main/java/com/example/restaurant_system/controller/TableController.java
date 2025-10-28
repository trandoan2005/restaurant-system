package com.example.restaurant_system.controller;

import com.example.restaurant_system.Service.TableService;
import com.example.restaurant_system.dto.ApiResponse;
import com.example.restaurant_system.entity.TableEntity;
import com.example.restaurant_system.enums.TableStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Cho phép frontend (React) truy cập
public class TableController {

    private final TableService tableService;

    // ✅ Lấy toàn bộ danh sách bàn
    @GetMapping
    public ResponseEntity<ApiResponse> getAllTables() {
        try {
            List<TableEntity> tables = tableService.getAllTables();
            return ResponseEntity.ok(ApiResponse.success("✅ Lấy danh sách bàn thành công", tables));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi khi lấy danh sách bàn: " + e.getMessage()));
        }
    }

    // ✅ Lấy thông tin 1 bàn theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getTableById(@PathVariable Long id) {
        try {
            return tableService.getTableById(id)
                    .map(table -> ResponseEntity.ok(ApiResponse.success("✅ Lấy thông tin bàn thành công", table)))
                    .orElse(ResponseEntity.status(404).body(ApiResponse.error("❌ Không tìm thấy bàn với ID: " + id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi: " + e.getMessage()));
        }
    }

    // ✅ Tạo bàn mới
    @PostMapping
    public ResponseEntity<ApiResponse> createTable(@RequestBody TableEntity table) {
        try {
            TableEntity saved = tableService.createTable(table);
            return ResponseEntity.status(201).body(ApiResponse.success("✅ Tạo bàn thành công", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi tạo bàn: " + e.getMessage()));
        }
    }

    // ✅ Cập nhật trạng thái bàn (frontend gửi JSON body)
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateTableStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            // Lấy giá trị "status" từ JSON body
            String statusStr = body.get("status");
            if (statusStr == null || statusStr.isBlank()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("❌ Thiếu trường 'status' trong body"));
            }

            TableStatus status = TableStatus.valueOf(statusStr.toUpperCase());
            TableEntity updated = tableService.updateTableStatus(id, status);

            return ResponseEntity.ok(ApiResponse.success("✅ Cập nhật trạng thái bàn thành công", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Trạng thái không hợp lệ: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi cập nhật trạng thái: " + e.getMessage()));
        }
    }

    // ✅ Lọc danh sách bàn theo trạng thái (ví dụ: /api/tables/status/AVAILABLE)
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse> getTablesByStatus(@PathVariable TableStatus status) {
        try {
            List<TableEntity> tables = tableService.getTablesByStatus(status);
            return ResponseEntity.ok(ApiResponse.success("✅ Lấy bàn theo trạng thái thành công", tables));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi: " + e.getMessage()));
        }
    }

    // ✅ Xóa bàn
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTable(@PathVariable Long id) {
        try {
            tableService.deleteTable(id);
            return ResponseEntity.ok(ApiResponse.success("✅ Xóa bàn thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("❌ Lỗi xóa bàn: " + e.getMessage()));
        }
    }
}
