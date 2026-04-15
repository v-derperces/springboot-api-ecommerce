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

import com.vderperces.ecommerce.dto.role.RoleRequest;
import com.vderperces.ecommerce.dto.role.RoleResponse;
import com.vderperces.ecommerce.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller for role-based endpoints.
 *
 * Provides operations for creating, reading, updating, and deleting roles.
 */
@RestController
@RequestMapping("/api/v1/admin/roles")
@Tag(name = "Roles", description = "Role management endpoints")
public class RoleController {

    /** Service for role business operations. */
    private final RoleService roleService;

    public RoleController(final RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Create a new role.
     *
     * @param request the role request payload
     * @return the created role response with HTTP 201
     */
    @Operation(summary = "Create role")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Role created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Role already exists")})
    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody final RoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.createRole(request));
    }

    /**
     * Get all roles.
     *
     * @return list of role responses with HTTP 200
     */
    @Operation(summary = "Get all roles")
    @ApiResponse(responseCode = "200", description = "Roles retrieved")
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getRoles() {
        return ResponseEntity.ok(this.roleService.getRoles());
    }

    /**
     * Get a role by id.
     *
     * @param id the role id
     * @return role response with HTTP 200
     */
    @Operation(summary = "Get role by id")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Role found"),
            @ApiResponse(responseCode = "404", description = "Role not found")})
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRole(
            @Parameter(description = "Role id", example = "1") @PathVariable final Long id) {
        return ResponseEntity.ok(this.roleService.getRole(id));
    }

    /**
     * Update a role by id.
     *
     * @param request the new role values
     * @param id the role id
     * @return updated role response with HTTP 200
     */
    @Operation(summary = "Update role")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Role updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "409", description = "Role already exists")})
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRole(@Valid @RequestBody final RoleRequest request,
            @Parameter(description = "Role id", example = "1") @PathVariable final Long id) {
        return ResponseEntity.ok(this.roleService.updateRole(id, request));
    }

    /**
     * Delete a role by id.
     *
     * @param id the role id
     * @return no content response with HTTP 204
     */
    @Operation(summary = "Delete role")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Role deleted"),
            @ApiResponse(responseCode = "409", description = "Role is linked to users")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "Role id", example = "1") @PathVariable final Long id) {
        this.roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
