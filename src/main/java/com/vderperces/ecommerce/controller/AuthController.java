package com.vderperces.ecommerce.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vderperces.ecommerce.dto.LoginRequest;
import com.vderperces.ecommerce.dto.user.UserCreateRequest;
import com.vderperces.ecommerce.dto.user.UserResponse;
import com.vderperces.ecommerce.service.AuthService;

import jakarta.validation.Valid;

/**
 * REST controller for authentication operations (login and registration).
 */
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    /**
     * Service used for authentication operations.
     */
    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user.
     *
     * @param request user create request
     * @return created user response with HTTP 201
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody final UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.authService.register(request));
    }

    /**
     * Authenticate a user and return a JWT token.
     *
     * @param request login request containing username/password
     * @return map with token string and HTTP 200
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody final LoginRequest request) {
        final String token = this.authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
