package fr.afpa.pompey.APIEcommerce.controller;

import fr.afpa.pompey.APIEcommerce.exceptionhandler.CustomHttpException;
import fr.afpa.pompey.APIEcommerce.model.User;
import fr.afpa.pompey.APIEcommerce.repository.RoleRepository;
import fr.afpa.pompey.APIEcommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(Authentication authentication) {
        return userService.getUserByEmail(authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/me")
    public ResponseEntity<String> updateMyInfo(@Valid @RequestBody User userUpdate,
                                               Authentication authentication) throws CustomHttpException {

        Optional<User> existingOpt = userService.getUserByEmail(authentication.getName());

        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User existing = existingOpt.get();
        existing.setFirstName(userUpdate.getFirstName());
        existing.setLastName(userUpdate.getLastName());
        existing.setEmail(userUpdate.getEmail());
        existing.setPhone(userUpdate.getPhone());
        existing.setAddress(userUpdate.getAddress());

        if (!userUpdate.getPassword().isBlank()) {
            PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            existing.setPassword(passwordEncoder.encode(userUpdate.getPassword()));
        }

        userService.saveUser(existing);
        return ResponseEntity.ok("User updated successfully");
    }
}
