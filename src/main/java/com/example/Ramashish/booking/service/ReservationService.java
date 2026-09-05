package com.example.Ramashish.booking.service;

import com.example.Ramashish.booking.dto.reservation.ReservationRequest;
import com.example.Ramashish.booking.dto.reservation.ReservationResponse;
import com.example.Ramashish.booking.exception.AccessDeniedCustomException;
import com.example.Ramashish.booking.exception.ResourceNotFoundException;
import com.example.Ramashish.booking.model.Reservation;
import com.example.Ramashish.booking.model.ReservationStatus;
import com.example.Ramashish.booking.model.Resource;
import com.example.Ramashish.booking.model.User;
import com.example.Ramashish.booking.repository.ReservationRepository;
import com.example.Ramashish.booking.repository.UserRepository;
import com.example.Ramashish.booking.specification.ReservationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ResourceService resourceService;
    public ReservationResponse create(String username, ReservationRequest request) {
        User user = getUserOrThrow(username);
        Resource resource = resourceService.getEntity(request.resourceId());
        BigDecimal price = calculatePrice(resource.getHourlyRate(), request.startTime(),
                 request.endTime());
        Reservation reservation = Reservation.builder()
                .user(user)
                .resource(resource)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .price(price)
                .status(ReservationStatus.PENDING)
                .build();
        return toResponse(reservationRepository.save(reservation));
    }
    public Page<ReservationResponse> findAll(String username, boolean isAdmin,
                                             ReservationStatus status, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Long ownerScopeId = isAdmin ? null : getUserOrThrow(username).getId();
        var spec = ReservationSpecification.build(status, minPrice, maxPrice, ownerScopeId);
        return reservationRepository.findAll(spec, pageable).map(this::toResponse);
    }
    public ReservationResponse findById(String username, boolean isAdmin, Long id) {
        Reservation reservation = getEntityOrThrow(id);
        assertOwnershipOrAdmin(reservation, username, isAdmin);
        return toResponse(reservation);
    }

    public ReservationResponse updateStatus(String username, boolean isAdmin, Long id,
 ReservationStatus newStatus) {

        Reservation reservation = getEntityOrThrow(id);
        assertOwnershipOrAdmin(reservation, username, isAdmin);
        reservation.setStatus(newStatus);
        return toResponse(reservationRepository.save(reservation));
    }
    public void cancel(String username, boolean isAdmin, Long id) {
        Reservation reservation = getEntityOrThrow(id);
        assertOwnershipOrAdmin(reservation, username, isAdmin);
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }
    private BigDecimal calculatePrice(BigDecimal hourlyRate, java.time.LocalDateTime start,
 java.time.LocalDateTime end) {
        long minutes = Duration.between(start, end).toMinutes();
        BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 4,
                java.math.RoundingMode.HALF_UP);
        return hourlyRate.multiply(hours).setScale(2, java.math.RoundingMode.HALF_UP);
    }
    private void assertOwnershipOrAdmin(Reservation reservation, String username, boolean isAdmin) {
        if (isAdmin) return;
        if (!reservation.getUser().getUsername().equals(username)) {
            throw new AccessDeniedCustomException("You do not have access to this reservation");
        }
    }
    private User getUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " +
                         username));
    }
    private Reservation getEntityOrThrow(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
    }
    private ReservationResponse toResponse(Reservation r) {
        return new ReservationResponse(
                r.getId(), r.getUser().getId(), r.getUser().getUsername(),
                r.getResource().getId(), r.getResource().getName(),
                r.getStartTime(), r.getEndTime(), r.getPrice(), r.getStatus(),
                r.getCreatedAt(), r.getUpdatedAt());
    }
}
