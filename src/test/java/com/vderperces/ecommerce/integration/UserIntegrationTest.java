package com.vderperces.ecommerce.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vderperces.ecommerce.dto.user.ChangePasswordRequest;
import com.vderperces.ecommerce.dto.user.UserUpdateRequest;
import com.vderperces.ecommerce.model.Role;
import com.vderperces.ecommerce.model.User;
import com.vderperces.ecommerce.repository.RoleRepository;
import com.vderperces.ecommerce.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserIntegrationTest {

    private static final String EMAIL = "harry@hogwarts.com";
    private static final String PASSWORD = "Test123!";

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
        createUser();
    }

    private User createUser() {
        Role role = roleRepository.findByName("USER").orElseThrow();

        User user = new User();
        user.setFirstName("Harry");
        user.setLastName("Potter");
        user.setEmail(EMAIL);
        user.setPassword(passwordEncoder.encode(PASSWORD));
        user.setActive(true);
        user.setRoles(List.of(role));

        return userRepository.save(user);
    }

    private String asJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "USER")
    void shouldReturnCurrentUser() throws Exception {

        mockMvc.perform(get("/api/v1/users")).andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.firstName").value("Harry"));
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "USER")
    void shouldUpdateUserProfile() throws Exception {

        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("NewFirst");
        request.setLastName("NewLast");
        request.setEmail("new@mail.com");

        mockMvc.perform(put("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request))).andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("NewFirst"))
                .andExpect(jsonPath("$.email").value("new@mail.com"));
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "USER")
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {

        Role role = roleRepository.findByName("USER").orElseThrow();

        User existing = new User();
        existing.setFirstName("Ron");
        existing.setLastName("Weasley");
        existing.setEmail("existing@mail.com");
        existing.setPassword(passwordEncoder.encode(PASSWORD));
        existing.setRoles(List.of(role));
        userRepository.save(existing);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Harry");
        request.setLastName("Potter");
        request.setEmail("existing@mail.com");

        mockMvc.perform(put("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request))).andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "USER")
    void shouldChangePassword() throws Exception {

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword(PASSWORD);
        request.setNewPassword("NewPass123!");

        mockMvc.perform(put("/api/v1/users/password").contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request))).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "USER")
    void shouldReturnBadRequestWhenCurrentPasswordInvalid() throws Exception {

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrong");
        request.setNewPassword("NewPass123!");

        mockMvc.perform(put("/api/v1/users/password").contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request))).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Current password is incorrect"));
    }

}
