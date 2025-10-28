package com.example.restaurant_system.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class PromotionRequest {
    private String name;
    private Double discountAmount;
    private Double discountPercentage;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}