package fr.afpa.pompey.APIEcommerce.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import fr.afpa.pompey.APIEcommerce.dto.LoginRequest;
import fr.afpa.pompey.APIEcommerce.dto.user.ChangePasswordRequest;
import fr.afpa.pompey.APIEcommerce.dto.user.UserCreateRequest;
import fr.afpa.pompey.APIEcommerce.dto.user.UserResponse;
import fr.afpa.pompey.APIEcommerce.dto.user.UserUpdateRequest;
import fr.afpa.pompey.APIEcommerce.service.UserService;
import jakarta.validation.Valid;

/**
 * REST controller for user authentication and profile operations.
 */
@RestController
public class UserController {

    /** Service used for user actions such as register, login and profile updates. */
    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * Register a new user.
     *
     * @param request user create request
     * @return created user response with HTTP 201
     */
    @PostMapping("api/auth/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody final UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.register(request));
    }

    /**
     * Authenticate a user and return a JWT token.
     *
     * @param request login request containing username/password
     * @return map with token string and HTTP 200
     */
    @PostMapping("api/auth/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody final LoginRequest request) {
        final String token = this.userService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(Map.of("token", token));
    }

    /**
     * Get current authenticated user details.
     *
     * @param authentication principal from security context
     * @return user response with HTTP 200
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(final Authentication authentication) {
        return ResponseEntity.ok(this.userService.getUserByEmail(authentication.getName()));
    }

    /**
     * Update current user profile.
     *
     * @param userUpdateRequest user update payload
     * @param authentication principal from security context
     * @return updated user response with HTTP 200
     */
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody final UserUpdateRequest userUpdateRequest,
            final Authentication authentication) {
        return ResponseEntity.ok(this.userService.updateUser(authentication.getName(), userUpdateRequest));
    }

    /**
     * Change the authenticated user password.
     *
     * @param changePasswordRequest new password payload
     * @param authentication principal from security context
     * @return no content response with HTTP 204
     */
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody final ChangePasswordRequest changePasswordRequest,
            final Authentication authentication) {
        this.userService.changePassword(authentication.getName(), changePasswordRequest);
        return ResponseEntity.noContent().build();
    }
}
