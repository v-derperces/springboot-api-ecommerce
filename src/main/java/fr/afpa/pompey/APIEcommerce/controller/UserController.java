package fr.afpa.pompey.APIEcommerce.controller;

import fr.afpa.pompey.APIEcommerce.dto.LoginRequest;
import fr.afpa.pompey.APIEcommerce.dto.user.ChangePasswordRequest;
import fr.afpa.pompey.APIEcommerce.dto.user.UserCreateRequest;
import fr.afpa.pompey.APIEcommerce.dto.user.UserResponse;
import fr.afpa.pompey.APIEcommerce.dto.user.UserUpdateRequest;
import fr.afpa.pompey.APIEcommerce.exceptionhandler.CustomHttpException;
import fr.afpa.pompey.APIEcommerce.service.JWTService;
import fr.afpa.pompey.APIEcommerce.service.UserService;
import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.userService = userService;
    }

    @PostMapping("api/auth/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserCreateRequest request) throws CustomHttpException {
        return ResponseEntity.status(HttpStatus.CREATED)
        .body(userService.register(request));
    }

    @PostMapping("api/auth/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        String token = userService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserByEmail(authentication.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody UserUpdateRequest userUpdateRequest, Authentication authentication) {
        return ResponseEntity.ok(userService.updateUser(authentication.getName(), userUpdateRequest));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest, Authentication authentication) {
        userService.changePassword(authentication.getName(), changePasswordRequest);
        return ResponseEntity.noContent().build();
    }
}
