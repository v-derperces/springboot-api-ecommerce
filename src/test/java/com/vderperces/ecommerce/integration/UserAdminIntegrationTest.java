package com.vderperces.ecommerce.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vderperces.ecommerce.dto.user.UserAdminCreateRequest;
import com.vderperces.ecommerce.dto.user.UserAdminResponse;
import com.vderperces.ecommerce.dto.user.UserAdminUpdateRequest;
import com.vderperces.ecommerce.model.User;
import com.vderperces.ecommerce.repository.RoleRepository;
import com.vderperces.ecommerce.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserAdminIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void createUserAsAdminShouldReturnCreatedAndPersisted() throws Exception {
        Long userRoleId = roleRepository.findByName("USER").orElseThrow().getRoleId();
        UserAdminCreateRequest request = new UserAdminCreateRequest();
        request.setFirstName("Integration");
        request.setLastName("Admin");
        request.setEmail("integration.admin@example.com");
        request.setPassword("pass1234");
        request.setRoles(List.of(userRoleId));

        String responseJson = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("integration.admin@example.com"))
                .andExpect(jsonPath("$.firstName").value("Integration"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserAdminResponse response = objectMapper.readValue(responseJson, UserAdminResponse.class);
        assertNotNull(response.getId());

        User saved = userRepository.findById(response.getId()).orElseThrow();
        assertEquals("Integration", saved.getFirstName());
        assertEquals("integration.admin@example.com", saved.getEmail());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void updateUserAsAdminShouldUpdateExistingUser() throws Exception {
        UserAdminResponse created = createUser("update.user@example.com");

        UserAdminUpdateRequest updateRequest = new UserAdminUpdateRequest();
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("Name");
        updateRequest.setEmail("update.user@example.com");
        updateRequest.setPhone("0101010101");
        updateRequest.setAddress("Some Street 1");
        updateRequest.setActive(false);
        updateRequest.setRoles(List.of(roleRepository.findByName("ADMIN").orElseThrow().getRoleId()));

        String responseJson = mockMvc.perform(put("/users/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.active").value(false))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserAdminResponse updated = objectMapper.readValue(responseJson, UserAdminResponse.class);
        assertEquals(created.getId(), updated.getId());

        User persisted = userRepository.findById(created.getId()).orElseThrow();
        assertEquals("Updated", persisted.getFirstName());
        assertEquals(false, persisted.isActive());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void deleteUserAsAdminShouldRemoveUser() throws Exception {
        UserAdminResponse created = createUser("delete.user@example.com");

        mockMvc.perform(delete("/users/" + created.getId()))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.findById(created.getId()).isPresent());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getUsersAsAdminShouldReturnList() throws Exception {
        createUser("list.user1@example.com");
        createUser("list.user2@example.com");

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    private UserAdminResponse createUser(String email) throws Exception {
        Long userRoleId = roleRepository.findByName("USER").orElseThrow().getRoleId();

        UserAdminCreateRequest request = new UserAdminCreateRequest();
        request.setFirstName("Integration");
        request.setLastName("Admin");
        request.setEmail(email);
        request.setPassword("pass1234");
        request.setRoles(List.of(userRoleId));

        String responseJson = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(responseJson, UserAdminResponse.class);
    }
}
