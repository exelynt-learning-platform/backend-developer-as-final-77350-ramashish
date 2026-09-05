package com.example.Ramashish.booking.config;


import com.example.Ramashish.booking.model.Resource;
import com.example.Ramashish.booking.model.Role;
import com.example.Ramashish.booking.model.User;
import com.example.Ramashish.booking.repository.ResourceRepository;
import com.example.Ramashish.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(User.builder()
                    .username("admin")
                    .email("admin@bookingapi.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .build());
        }
        if (userRepository.findByUsername("user").isEmpty()) {
            userRepository.save(User.builder()
                    .username("user")
                    .email("user@bookingapi.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.ROLE_USER)
                    .build());
        }
        if (resourceRepository.count() == 0) {
            resourceRepository.save(Resource.builder()
                    .name("Conference Room A")
                    .description("Seats 12, projector, whiteboard")
                    .category("Meeting Room")
                    .hourlyRate(new BigDecimal("25.00"))
                    .build());
            resourceRepository.save(Resource.builder()
                    .name("Tesla Model 3")
                    .description("Company pool EV, full charge guaranteed")
                    .category("Vehicle")
                    .hourlyRate(new BigDecimal("40.00"))
                    .build());
            resourceRepository.save(Resource.builder()
                    .name("4K Projector")
                    .description("Portable, HDMI + wireless casting")
                    .category("Equipment")
                    .hourlyRate(new BigDecimal("10.00"))
                    .build());
        }
    }
}
