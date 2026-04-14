package com.vderperces.ecommerce.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vderperces.ecommerce.dto.LoginRequest;
import com.vderperces.ecommerce.dto.user.UserCreateRequest;
import com.vderperces.ecommerce.exceptions.AuthException;
import com.vderperces.ecommerce.exceptions.ConflictException;
import com.vderperces.ecommerce.service.AuthService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    void loginWithInvalidCredentialsShouldReturn401() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setUsername("tom.riddles@horcrux.com");
        login.setPassword("nagini");

        // Simulate invalid credentials
        when(authService.login(login.getUsername(), login.getPassword()))
                .thenThrow(new AuthException("Invalid credentials", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void loginWithValidCredentialsShouldReturnToken() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setUsername("tom.riddles@horcrux.com");
        login.setPassword("nagini");

        // Simulate valid login returning JWT token
        when(authService.login(login.getUsername(), login.getPassword()))
                .thenReturn("dummy-jwt-token");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-jwt-token"));
    }

    @Test
    void registerWithDuplicateEmailShouldReturn409() throws Exception {
        UserCreateRequest register = new UserCreateRequest();
        register.setFirstName("Albus");
        register.setLastName("Dumbledore");
        register.setEmail("phoenix@hogwarts.com");
        register.setPassword("password");

        // Simulate duplicate email
        when(authService.register(any(UserCreateRequest.class)))
                .thenThrow(new ConflictException("Email already exists"));

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }
}
