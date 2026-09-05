package com.example.Ramashish.booking.validation;

import com.example.Ramashish.booking.dto.reservation.ReservationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, ReservationRequest> {
    @Override
    public boolean isValid(ReservationRequest request, ConstraintValidatorContext context){
        if(request == null || request.startTime() == null || request.endTime() == null){
            return true;
        }
        return request.endTime().isAfter(request.startTime());
    }
}
