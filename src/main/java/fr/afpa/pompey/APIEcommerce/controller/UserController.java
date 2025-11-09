package fr.afpa.pompey.APIEcommerce.controller;

import fr.afpa.pompey.APIEcommerce.exceptionhandler.CustomHttpException;
import fr.afpa.pompey.APIEcommerce.model.Role;
import fr.afpa.pompey.APIEcommerce.model.User;
import fr.afpa.pompey.APIEcommerce.repository.RoleRepository;
import fr.afpa.pompey.APIEcommerce.repository.UserRepository;
import fr.afpa.pompey.APIEcommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    public UserController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/user")
    public User createUser(@Valid @RequestBody User user) throws CustomHttpException {
        Role userRole = (Role) roleRepository.findRoleByName("USER")
                .orElseThrow(() -> new CustomHttpException("Role USER not found",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error"));

        user.setRoles(new ArrayList<>(List.of(userRole)));

        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userService.saveUser(user);
    }

    @GetMapping("/users")
    public Iterable<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable("id") int id) {
        Optional<User> user = userService.getUser(id);
        return user.orElse(null);
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
    public void deleteUser(@PathVariable("id") int id) throws CustomHttpException {
        userService.deleteUser(id);
    }
}
