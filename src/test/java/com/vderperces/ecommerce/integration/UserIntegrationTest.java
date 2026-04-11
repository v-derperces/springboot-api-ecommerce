package com.vderperces.ecommerce.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vderperces.ecommerce.dto.LoginRequest;
import com.vderperces.ecommerce.dto.user.ChangePasswordRequest;
import com.vderperces.ecommerce.dto.user.UserCreateRequest;
import com.vderperces.ecommerce.model.Role;
import com.vderperces.ecommerce.model.User;
import com.vderperces.ecommerce.repository.RoleRepository;
import com.vderperces.ecommerce.repository.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void registerShouldCreateUserValid() throws Exception {
        Role userRole = roleRepository.findByName("USER").get(); // Role configured in data.sql

        String email = "ron.weasly@hogwarts.com";
        String rawPassword = "TestPass123";

        UserCreateRequest request = new UserCreateRequest();
        request.setFirstName("Ron");
        request.setLastName("Weasly");
        request.setEmail(email);
        request.setPassword(rawPassword);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.firstName").value("Ron"))
                .andExpect(jsonPath("$.lastName").value("Weasly"));

        User saved = userRepository.findByEmail(email).orElseThrow();
        assert (saved.getFirstName()).equals("Ron");
        assert (saved.getLastName()).equals("Weasly");
        assert (passwordEncoder.matches(rawPassword, saved.getPassword()));
        assert (saved.getRoles()).contains(userRole);
    }

    @Test
    void registerDuplicateEmailShouldReturnConflict() throws Exception {
        Role userRole = roleRepository.findByName("USER").get();

        String email = "hermione@hogwarts.com";

        // Existing user
        User existing = new User();
        existing.setFirstName("Hermione");
        existing.setLastName("Granger");
        existing.setEmail(email);
        existing.setPassword(passwordEncoder.encode("Password1"));
        existing.setRoles(List.of(userRole));
        userRepository.save(existing);

        UserCreateRequest request = new UserCreateRequest();
        request.setFirstName("Hermione");
        request.setLastName("Granger");
        request.setEmail(email);
        request.setPassword("Password1");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Unable to create account. Please check the provided information."));
    }

    @Test
    void loginWithValidCredentialsShouldReturnToken() throws Exception {
        Role userRole = roleRepository.findByName("USER").get();

        String email = "harry@hogwarts.com";
        String rawPassword = "Test123!";

        User user = new User();
        user.setFirstName("Harry");
        user.setLastName("Potter");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRoles(List.of(userRole));
        userRepository.save(user);

        LoginRequest login = new LoginRequest();
        login.setUsername(email);
        login.setPassword(rawPassword);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void loginWithInvalidCredentialsShouldReturnUnauthorized() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setUsername("nonexistent@hogwarts.com");
        login.setPassword("wrongpass");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void changePasswordValidShouldReturnNoContent() throws Exception {
        Role userRole = roleRepository.findByName("USER").get();

        String email = "luna@hogwarts.com";
        String oldPassword = "OldPass123";
        String newPassword = "NewPass123";

        User user = new User();
        user.setFirstName("Luna");
        user.setLastName("Lovegood");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(oldPassword));
        user.setRoles(List.of(userRole));
        userRepository.save(user);

        LoginRequest login = new LoginRequest();
        login.setUsername(email);
        login.setPassword(oldPassword);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        ChangePasswordRequest dto = new ChangePasswordRequest();
        dto.setCurrentPassword(oldPassword);
        dto.setNewPassword(newPassword);

        String token = result.getResponse().getContentAsString().replace("{\"token\":\"", "").replace("\"}", "");

        mockMvc.perform(put("/api/v1/users/me/password").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        User updated = userRepository.findByEmail(email).orElseThrow();
        assert (passwordEncoder.matches(newPassword, updated.getPassword()));
    }

    @Test
    void changePasswordWrongCurrentPasswordShouldReturnBadRequest() throws Exception {
        Role userRole = roleRepository.findByName("USER").get();

        String email = "neville@hogwarts.com";
        String oldPassword = "CorrectPass123";

        User user = new User();
        user.setFirstName("Neville");
        user.setLastName("Longbottom");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(oldPassword));
        user.setRoles(List.of(userRole));
        userRepository.save(user);

        LoginRequest login = new LoginRequest();
        login.setUsername(email);
        login.setPassword(oldPassword);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getContentAsString().replace("{\"token\":\"", "").replace("\"}", "");

        ChangePasswordRequest dto = new ChangePasswordRequest();
        dto.setCurrentPassword("WrongPass");
        dto.setNewPassword("NewPass123");

        mockMvc.perform(put("/api/v1/users/me/password").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Current password is incorrect"));
    }

}
