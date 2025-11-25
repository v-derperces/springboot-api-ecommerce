package fr.afpa.pompey.APIEcommerce.controller;

import fr.afpa.pompey.APIEcommerce.dto.LoginRequest;
import fr.afpa.pompey.APIEcommerce.exceptionhandler.CustomHttpException;
import fr.afpa.pompey.APIEcommerce.model.Role;
import fr.afpa.pompey.APIEcommerce.model.User;
import fr.afpa.pompey.APIEcommerce.repository.RoleRepository;
import fr.afpa.pompey.APIEcommerce.service.JWTService;
import fr.afpa.pompey.APIEcommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AuthentificationController {

    private final RoleRepository roleRepository;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthentificationController(RoleRepository roleRepository,
                                      UserService userService, AuthenticationManager authenticationManager,
                                      JWTService jwtService, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            String token = jwtService.generateToken(authentication);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody User user) throws CustomHttpException {

        Role userRole = (Role) roleRepository.findRoleByName("USER")
                .orElseThrow(() -> new CustomHttpException("Role USER not found",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error"));

        user.setRoles(new ArrayList<>(List.of(userRole)));

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully");
    }
}
