package com.example.Ramashish.booking.controller;

import com.example.Ramashish.booking.dto.reservation.ReservationRequest;
import com.example.Ramashish.booking.dto.reservation.ReservationResponse;
import com.example.Ramashish.booking.model.ReservationStatus;
import com.example.Ramashish.booking.service.ReservationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations")
@SecurityRequirement(name = "bearerAuth")
public class ReservationController {

    private final ReservationService reservationService;
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ReservationResponse> create(@AuthenticationPrincipal UserDetails userDetails,
                                                      @Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.create(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Page<ReservationResponse>> findAll(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Pageable pageable) {
        boolean isAdmin = isAdmin(userDetails);
        Page<ReservationResponse> page = reservationService.findAll(
                userDetails.getUsername(), isAdmin, status, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(page);
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ReservationResponse> findById(@AuthenticationPrincipal
   UserDetails userDetails,
                                                        @PathVariable Long id) {
        boolean isAdmin = isAdmin(userDetails);
        return ResponseEntity.ok(reservationService.findById(userDetails.getUsername(),
                   isAdmin, id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ReservationResponse> updateStatus(@AuthenticationPrincipal
   UserDetails userDetails,
                                                            @PathVariable Long id,
                                                            @RequestParam ReservationStatus
   status) {
        boolean isAdmin = isAdmin(userDetails);
        return ResponseEntity.ok(reservationService.updateStatus(userDetails.getUsername(),
                   isAdmin, id, status));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Void> cancel(@AuthenticationPrincipal UserDetails userDetails,
   @PathVariable Long id) {
        boolean isAdmin = isAdmin(userDetails);
        reservationService.cancel(userDetails.getUsername(), isAdmin, id);
        return ResponseEntity.noContent().build();
    }
    private boolean isAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));
    }

}
