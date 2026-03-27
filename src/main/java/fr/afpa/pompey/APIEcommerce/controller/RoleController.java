package fr.afpa.pompey.APIEcommerce.controller;

import fr.afpa.pompey.APIEcommerce.dto.role.RoleRequest;
import fr.afpa.pompey.APIEcommerce.dto.role.RoleResponse;
import fr.afpa.pompey.APIEcommerce.service.RoleService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(roleService.createRole(request));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getRoles() {
        return ResponseEntity.ok(roleService.getRoles());
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<RoleResponse> getRole(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRole(id));
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<RoleResponse> updateRole(@Valid @RequestBody RoleRequest request, @PathVariable Long id) {
        return ResponseEntity.ok(roleService.updateRole(id, request));
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
