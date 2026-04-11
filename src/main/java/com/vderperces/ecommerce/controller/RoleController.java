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
import org.springframework.web.bind.annotation.RestController;

import com.vderperces.ecommerce.dto.role.RoleRequest;
import com.vderperces.ecommerce.dto.role.RoleResponse;
import com.vderperces.ecommerce.service.RoleService;

import jakarta.validation.Valid;

/**
 * REST controller for role-based endpoints.
 *
 * Provides operations for creating, reading, updating, and deleting roles.
 */
@RestController
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
    @PostMapping("/roles")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody final RoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(this.roleService.createRole(request));
    }

    /**
     * Get all roles.
     *
     * @return list of role responses with HTTP 200
     */
    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getRoles() {
        return ResponseEntity.ok(this.roleService.getRoles());
    }

    /**
     * Get a role by id.
     *
     * @param id the role id
     * @return role response with HTTP 200
     */
    @GetMapping("/roles/{id}")
    public ResponseEntity<RoleResponse> getRole(@PathVariable final Long id) {
        return ResponseEntity.ok(this.roleService.getRole(id));
    }

    /**
     * Update a role by id.
     *
     * @param request the new role values
     * @param id      the role id
     * @return updated role response with HTTP 200
     */
    @PutMapping("/roles/{id}")
    public ResponseEntity<RoleResponse> updateRole(@Valid @RequestBody final RoleRequest request,
            @PathVariable final Long id) {
        return ResponseEntity.ok(this.roleService.updateRole(id, request));
    }

    /**
     * Delete a role by id.
     *
     * @param id the role id
     * @return no content response with HTTP 204
     */
    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable final Long id) {
        this.roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
