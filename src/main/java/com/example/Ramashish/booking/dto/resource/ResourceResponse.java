package com.example.Ramashish.booking.dto.resource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ResourceResponse(Long id,
                               String name,
                               String description,
                               String category,
                               BigDecimal hourlyRate,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt) {
}
