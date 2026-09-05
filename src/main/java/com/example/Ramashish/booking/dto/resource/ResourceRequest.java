package com.example.Ramashish.booking.dto.resource;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ResourceRequest(@NotBlank(message = "Name is required") String name,
                              String description,
                              String category,
                              @DecimalMin(value = "0.01", message = "Hourly rate must be greater than 0")
                              BigDecimal hourlyRate) {
}
