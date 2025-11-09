package fr.afpa.pompey.APIEcommerce.controller;

import fr.afpa.pompey.APIEcommerce.exceptionhandler.CustomHttpException;
import fr.afpa.pompey.APIEcommerce.model.Role;
import fr.afpa.pompey.APIEcommerce.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/role")
    public Role createRole(@Valid @RequestBody Role role) throws CustomHttpException {
        return roleService.saveRole(role);
    }

    @GetMapping("/roles")
    public Iterable<Role> getRoles() {
        return roleService.getRoles();
    }

    @GetMapping("/role/{id}")
    public Role getRole(@PathVariable int id) {
        Optional<Role> r = roleService.getRole(id);
        return r.orElse(null);
    }

    @PutMapping("/role/{id}")
    public Role updateRole(@Valid @RequestBody Role role, @PathVariable int id) throws CustomHttpException {
        Optional<Role> existingOpt = roleService.getRole(id);
        if (existingOpt.isPresent()) {
            Role existing = existingOpt.get();
            existing.setName(role.getName());
            return roleService.saveRole(existing);
        }
        return null;
    }

    @DeleteMapping("/role/{id}")
    public void deleteRole(@PathVariable int id) throws CustomHttpException {
        roleService.deleteRole(id);
    }
}
