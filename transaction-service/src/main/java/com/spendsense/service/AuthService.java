package com.spendsense.service;

import com.spendsense.dto.AuthResponse;
import com.spendsense.dto.LoginRequest;
import com.spendsense.dto.RegisterRequest;
import com.spendsense.model.User;
import com.spendsense.repository.UserRepository;
import com.spendsense.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {

        // Check if userId already taken
        if (userRepository.existsByUserId(
                request.getUserId())) {
            throw new RuntimeException(
                    "UserId already taken: "
                            + request.getUserId());
        }

        // Check if email already registered
        if (userRepository.existsByEmail(
                request.getEmail())) {
            throw new RuntimeException(
                    "Email already registered: "
                            + request.getEmail());
        }

        // Hash the password — never store plain text
        User user = new User();
        user.setUserId(request.getUserId());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        log.info("✅ New user registered: {}",
                request.getUserId());

        // Generate token immediately after register
        String token = jwtService
                .generateToken(request.getUserId());

        return new AuthResponse(
                request.getUserId(),
                request.getEmail(),
                token
        );
    }

    public AuthResponse login(LoginRequest request) {

        // Find user by userId
        User user = userRepository
                .findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        "User not found: "
                                + request.getUserId()));

        // Verify password against stored hash
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new RuntimeException(
                    "Invalid password");
        }

        log.info("✅ User logged in: {}",
                request.getUserId());

        String token = jwtService
                .generateToken(request.getUserId());

        return new AuthResponse(
                user.getUserId(),
                user.getEmail(),
                token
        );
    }
}