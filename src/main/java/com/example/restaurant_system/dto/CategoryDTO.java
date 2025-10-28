package com.example.restaurant_system.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
    private Long id;
    private String title;
    private String description;
    private String photo;
}