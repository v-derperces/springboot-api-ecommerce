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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller for user profile operations.
 */
@RequestMapping("/api/v1/users")
@RestController
@Tag(name = "User", description = "User profile operations")
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
    @Operation(summary = "Get current user profile")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "User retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    @GetMapping
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
    @Operation(summary = "Update current user profile")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Email already used"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @PutMapping
    public ResponseEntity<UserResponse> updateMe(
            @Valid @RequestBody final UserUpdateRequest userUpdateRequest,
            final Authentication authentication) {
        return ResponseEntity
                .ok(this.userService.updateUser(authentication.getName(), userUpdateRequest));
    }

    /**
     * Change the authenticated user password.
     *
     * @param changePasswordRequest new password payload
     * @param authentication principal from security context
     * @return no content response with HTTP 204
     */
    @Operation(summary = "Change user password")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Password changed"),
            @ApiResponse(responseCode = "400", description = "Current password incorrect"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody final ChangePasswordRequest changePasswordRequest,
            final Authentication authentication) {
        this.userService.changePassword(authentication.getName(), changePasswordRequest);
        return ResponseEntity.noContent().build();
    }
}
