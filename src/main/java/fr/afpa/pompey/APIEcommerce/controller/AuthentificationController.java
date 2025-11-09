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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/auth")
public class AuthentificationController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;

    public AuthentificationController(UserRepository userRepository, RoleRepository roleRepository, UserService userService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody User user) throws CustomHttpException {

        Role userRole = (Role) roleRepository.findRoleByName("USER")
                .orElseThrow(() -> new CustomHttpException("Role USER not found",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error"));

        user.setRoles(new ArrayList<>(List.of(userRole)));

        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> body){
        String email = body.get("email");
        String password = body.get("password");

        Optional<User> userOpt = userRepository.getUserByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        User user = userOpt.get();
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        return ResponseEntity.ok("Login successful");
    }
}
