package com.vderperces.ecommerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vderperces.ecommerce.dto.user.UserAdminCreateRequest;
import com.vderperces.ecommerce.dto.user.UserAdminResponse;
import com.vderperces.ecommerce.dto.user.UserAdminUpdateRequest;
import com.vderperces.ecommerce.service.UserAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller for administrator user management.
 *
 * Provides endpoints to manage users with admin privileges.
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@Tag(name = "Admin - Users", description = "Administrator user management operations")
public class UserAdminController {

    /** Service for admin user operations. */
    private final UserAdminService userAdminService;

    public UserAdminController(final UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    /**
     * Create a new user by admin.
     *
     * @param request user creation payload
     * @return created user admin response with HTTP 201
     */
    @Operation(summary = "Create user (admin)")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Conflict (email already exists)")})
    @PostMapping
    public ResponseEntity<UserAdminResponse> createUser(
            @Valid @RequestBody final UserAdminCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.userAdminService.createUser(request));
    }

    /**
     * Get all users.
     *
     * @return list of user responses with additional information for admins with HTTP 200
     */
    @Operation(summary = "Get all users (admin)")
    @ApiResponse(responseCode = "200", description = "Users retrieved")
    @GetMapping
    public ResponseEntity<List<UserAdminResponse>> getUsers() {
        return ResponseEntity.ok(this.userAdminService.getUsers());
    }

    /**
     * Get one user by Id.
     *
     * @param id user id
     * @return user response with HTTP 200
     */
    @Operation(summary = "Get user by id (admin)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    @GetMapping("/{id}")
    public ResponseEntity<UserAdminResponse> getUser(@PathVariable final Long id) {
        return ResponseEntity.ok(this.userAdminService.getUser(id));
    }

    /**
     * Update an existing user.
     *
     * @param request updated details
     * @param id user id
     * @return updated user admin response with HTTP 200
     */
    @Operation(summary = "Update user (admin)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Conflict (data already used)")})
    @PutMapping("/{id}")
    public ResponseEntity<UserAdminResponse> updateUser(
            @Valid @RequestBody final UserAdminUpdateRequest request, @PathVariable final Long id) {
        return ResponseEntity.ok(this.userAdminService.updateUser(id, request));
    }

    /**
     * Delete a user by id.
     *
     * @param id user id
     * @return no content response with HTTP 204
     */
    @Operation(summary = "Delete user (admin)")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "409", description = "User has associated orders")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable final Long id) {
        this.userAdminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
