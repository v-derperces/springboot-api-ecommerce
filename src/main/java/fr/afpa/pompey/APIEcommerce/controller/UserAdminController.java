package fr.afpa.pompey.APIEcommerce.controller;

import fr.afpa.pompey.APIEcommerce.dto.user.UserAdminCreateRequest;
import fr.afpa.pompey.APIEcommerce.dto.user.UserAdminResponse;
import fr.afpa.pompey.APIEcommerce.dto.user.UserAdminUpdateRequest;
import fr.afpa.pompey.APIEcommerce.exceptionhandler.CustomHttpException;
import fr.afpa.pompey.APIEcommerce.service.UserAdminService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserAdminController {

    private final UserAdminService userAdminService;

    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserAdminResponse> createUser(@Valid @RequestBody UserAdminCreateRequest request) throws CustomHttpException {
            return ResponseEntity.status(HttpStatus.CREATED.value()).body(userAdminService.createUser(request));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserAdminResponse>> getUsers() {
        return ResponseEntity.ok(userAdminService.getUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserAdminResponse> getUser(@PathVariable Long id) throws CustomHttpException {
        return ResponseEntity.ok(userAdminService.getUser(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(
            @Valid @RequestBody UserAdminUpdateRequest request,
            @PathVariable("id") Long id) throws CustomHttpException {

        userAdminService.updateUser(id, request);
        return ResponseEntity.ok("User updated successfully");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) throws CustomHttpException {
        userAdminService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
