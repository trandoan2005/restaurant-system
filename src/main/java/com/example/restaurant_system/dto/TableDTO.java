package com.example.restaurant_system.dto;

import com.example.restaurant_system.enums.TableStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableDTO {
    private Long id;
    private String name;
    private String description;
    private Integer capacity;
    private TableStatus status;
}