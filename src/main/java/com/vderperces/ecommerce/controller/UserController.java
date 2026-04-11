package com.vderperces.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vderperces.ecommerce.dto.user.ChangePasswordRequest;
import com.vderperces.ecommerce.dto.user.UserResponse;
import com.vderperces.ecommerce.dto.user.UserUpdateRequest;
import com.vderperces.ecommerce.service.UserService;

import jakarta.validation.Valid;

/**
 * REST controller for user profile operations.
 */
@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    /**
     * Service used for user profile updates.
     */
    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
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
     * @param authentication    principal from security context
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
     * @param authentication        principal from security context
     * @return no content response with HTTP 204
     */
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody final ChangePasswordRequest changePasswordRequest,
            final Authentication authentication) {
        this.userService.changePassword(authentication.getName(), changePasswordRequest);
        return ResponseEntity.noContent().build();
    }
}
