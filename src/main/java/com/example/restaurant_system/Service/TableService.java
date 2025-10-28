package com.example.restaurant_system.Service;

import com.example.restaurant_system.entity.TableEntity;
import com.example.restaurant_system.enums.TableStatus;
import com.example.restaurant_system.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;

    public List<TableEntity> getAllTables() {
        return tableRepository.findAll();
    }

    public Optional<TableEntity> getTableById(Long id) {
        return tableRepository.findById(id);
    }

    public TableEntity createTable(TableEntity table) {
        // ✅ Validate table name uniqueness
        if (tableRepository.existsByName(table.getName())) {
            throw new RuntimeException("❌ Tên bàn đã tồn tại: " + table.getName());
        }

        // ✅ Set default status if not provided - ĐÃ SỬA FREE -> AVAILABLE
        if (table.getStatus() == null) {
            table.setStatus(TableStatus.AVAILABLE);
        }

        return tableRepository.save(table);
    }

    public TableEntity updateTable(Long id, TableEntity tableDetails) {
        return tableRepository.findById(id)
                .map(table -> {
                    // ✅ Check name uniqueness (if name changed)
                    if (!table.getName().equals(tableDetails.getName()) && 
                        tableRepository.existsByName(tableDetails.getName())) {
                        throw new RuntimeException("❌ Tên bàn đã tồn tại: " + tableDetails.getName());
                    }

                    table.setName(tableDetails.getName());
                    table.setDescription(tableDetails.getDescription());
                    table.setCapacity(tableDetails.getCapacity());
                    table.setStatus(tableDetails.getStatus());
                    
                    return tableRepository.save(table);
                })
                .orElseThrow(() -> new RuntimeException("❌ Bàn không tồn tại với ID: " + id));
    }

    public void deleteTable(Long id) {
        TableEntity table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Bàn không tồn tại"));
        
        // ✅ Check if table is occupied before deletion - ĐÃ SỬA FREE -> AVAILABLE
        if (table.getStatus() == TableStatus.OCCUPIED) {
            throw new RuntimeException("❌ Không thể xóa bàn đang có khách");
        }
        
        tableRepository.deleteById(id);
    }

    // ✅ THÊM: Cập nhật trạng thái bàn
    public TableEntity updateTableStatus(Long id, TableStatus status) {
        return tableRepository.findById(id)
                .map(table -> {
                    table.setStatus(status);
                    return tableRepository.save(table);
                })
                .orElseThrow(() -> new RuntimeException("❌ Bàn không tồn tại"));
    }

    // ✅ THÊM: Lấy bàn theo trạng thái
    public List<TableEntity> getTablesByStatus(TableStatus status) {
        return tableRepository.findByStatus(status);
    }

    // ✅ THÊM: Lấy bàn theo sức chứa
    public List<TableEntity> getTablesByCapacity(Integer minCapacity) {
        return tableRepository.findByCapacityGreaterThanEqual(minCapacity);
    }

    // ✅ THÊM: Lấy bàn trống - ĐÃ SỬA FREE -> AVAILABLE
    public List<TableEntity> getAvailableTables() {
        return tableRepository.findByStatus(TableStatus.AVAILABLE);
    }

    // ✅ THÊM: Lấy bàn đang có khách
    public List<TableEntity> getOccupiedTables() {
        return tableRepository.findByStatus(TableStatus.OCCUPIED);
    }

    // ✅ THÊM: Đếm số bàn theo trạng thái
    public long countTablesByStatus(TableStatus status) {
        return tableRepository.findByStatus(status).size();
    }

    // ✅ THÊM: Tìm bàn phù hợp với số lượng khách - ĐÃ SỬA FREE -> AVAILABLE
    public List<TableEntity> findSuitableTables(Integer numberOfGuests) {
        return tableRepository.findByCapacityGreaterThanEqual(numberOfGuests)
                .stream()
                .filter(table -> table.getStatus() == TableStatus.AVAILABLE)
                .toList();
    }

    // ✅ THÊM: Đặt bàn (chuyển trạng thái từ AVAILABLE sang OCCUPIED) - ĐÃ SỬA
    public TableEntity reserveTable(Long tableId) {
        return tableRepository.findById(tableId)
                .map(table -> {
                    if (table.getStatus() != TableStatus.AVAILABLE) {
                        throw new RuntimeException("❌ Bàn không khả dụng để đặt");
                    }
                    table.setStatus(TableStatus.OCCUPIED);
                    return tableRepository.save(table);
                })
                .orElseThrow(() -> new RuntimeException("❌ Bàn không tồn tại"));
    }

    // ✅ THÊM: Giải phóng bàn (chuyển trạng thái từ OCCUPIED sang AVAILABLE) - ĐÃ SỬA
    public TableEntity freeTable(Long tableId) {
        return tableRepository.findById(tableId)
                .map(table -> {
                    if (table.getStatus() != TableStatus.OCCUPIED) {
                        throw new RuntimeException("❌ Bàn không đang được sử dụng");
                    }
                    table.setStatus(TableStatus.AVAILABLE);
                    return tableRepository.save(table);
                })
                .orElseThrow(() -> new RuntimeException("❌ Bàn không tồn tại"));
    }
}