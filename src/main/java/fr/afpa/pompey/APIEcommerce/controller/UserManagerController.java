package fr.afpa.pompey.APIEcommerce.controller;

import fr.afpa.pompey.APIEcommerce.exceptionhandler.CustomHttpException;
import fr.afpa.pompey.APIEcommerce.model.Role;
import fr.afpa.pompey.APIEcommerce.model.User;
import fr.afpa.pompey.APIEcommerce.repository.RoleRepository;
import fr.afpa.pompey.APIEcommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
public class UserManagerController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    private static final Logger LOGGER = Logger.getLogger(UserManagerController.class.getName());

    public UserManagerController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) throws CustomHttpException {
        if (isSensitiveUser(user) && !hasRoleAdmin()) {
            LOGGER.warning("Access denied for user " + getCurrentUsername() + " while trying to read sensitive data " +
                    "of user " + user.getUserId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        Role userRole = (Role) roleRepository.findRoleByName("USER")
                .orElseThrow(() -> new CustomHttpException("Role USER not found",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error"));

        user.setRoles(new ArrayList<>(List.of(userRole)));

        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userService.saveUser(user);
        return ResponseEntity.ok("User created successfully");
    }

    @GetMapping("/users")
    public Iterable<User> getUsers() {
        if (!hasRoleAdmin()) {
            return userService.getUsersByRole("USER");
        }
        return userService.getUsers();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUser(@PathVariable int id) {
        var userOpt = userService.getUser(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOpt.get();

        if (isSensitiveUser(user) && !hasRoleAdmin()) {
            LOGGER.warning("Access denied for user " + getCurrentUsername() + " while trying to read sensitive data " +
                    "of user " + user.getUserId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        return ResponseEntity.ok(user);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<String> updateUser(
            @Valid @RequestBody User user,
            @PathVariable("id") int id) throws CustomHttpException {

        Optional<User> existingOpt = userService.getUser(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User existing = existingOpt.get();

        if (isSensitiveUser(existing) && !hasRoleAdmin()) {
            LOGGER.warning("Access denied for user " + getCurrentUsername() + " while trying to update sensitive data" +
                    " of user " + user.getUserId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        existing.setLastName(user.getLastName());
        existing.setFirstName(user.getFirstName());
        existing.setEmail(user.getEmail());

        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        if (!user.getPassword().isBlank() &&
                !passwordEncoder.matches(user.getPassword(), existing.getPassword())) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (user.getPhone() != null) {
            existing.setPhone(user.getPhone());
        }

        if (user.getAddress() != null) {
            existing.setAddress(user.getAddress());
        }

        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            existing.setRoles(user.getRoles());
        }

        userService.saveUser(existing);

        return ResponseEntity.ok("User updated successfully");
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) throws CustomHttpException {
        var userOpt = userService.getUser(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOpt.get();

        if (isSensitiveUser(user) && !hasRoleAdmin()) {
            LOGGER.warning("Access denied for user " + getCurrentUsername() + " while trying to delete sensitive data" +
                    " of user " + user.getUserId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Managers cannot delete admins/managers");
        }

        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    private boolean hasRoleAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }

    private boolean isSensitiveUser(User user) {
        return user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN") || r.getName().equals("MANAGER"));
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "anonymous";
        }
        return auth.getName();
    }
}
