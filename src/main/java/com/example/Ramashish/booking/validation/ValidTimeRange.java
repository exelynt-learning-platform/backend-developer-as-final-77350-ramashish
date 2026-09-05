package com.example.Ramashish.booking.validation;

import jakarta.validation.Payload;

public @interface ValidTimeRange {
    String message() default "End time musr be after start time";
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default{};
}
