package fr.afpa.pompey.APIEcommerce.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.afpa.pompey.APIEcommerce.dto.role.RoleResponse;
import fr.afpa.pompey.APIEcommerce.dto.user.UserAdminCreateRequest;
import fr.afpa.pompey.APIEcommerce.dto.user.UserAdminResponse;
import fr.afpa.pompey.APIEcommerce.dto.user.UserAdminUpdateRequest;
import fr.afpa.pompey.APIEcommerce.service.UserAdminService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserAdminService userAdminService;

    @Test
    void createUserUnauthenticatedShouldReturn401() throws Exception {
        UserAdminCreateRequest createRequest = new UserAdminCreateRequest();
        createRequest.setFirstName("Any");
        createRequest.setLastName("User");
        createRequest.setEmail("any.user@example.com");
        createRequest.setPassword("password123");
        createRequest.setRoles(List.of(1L));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createUserAsUserShouldReturn403() throws Exception {
        UserAdminCreateRequest createRequest = new UserAdminCreateRequest();
        createRequest.setFirstName("Any");
        createRequest.setLastName("User");
        createRequest.setEmail("any.user@example.com");
        createRequest.setPassword("password123");
        createRequest.setRoles(List.of(1L));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUserAsAdminShouldReturn201() throws Exception {
        UserAdminCreateRequest createRequest = new UserAdminCreateRequest();
        createRequest.setFirstName("Admin");
        createRequest.setLastName("User");
        createRequest.setEmail("admin.user@example.com");
        createRequest.setPassword("password123");
        createRequest.setRoles(List.of(1L));

        UserAdminResponse response = new UserAdminResponse();
        response.setId(1L);
        response.setFirstName("Admin");
        response.setLastName("User");
        response.setEmail("admin.user@example.com");

        when(userAdminService.createUser(any(UserAdminCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("admin.user@example.com"));
    }

    @Test
    void getUsersUnauthenticatedShouldReturn401() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUsersAsUserShouldReturn403() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsersAsAdminShouldReturn200() throws Exception {
        UserAdminResponse response = new UserAdminResponse();
        response.setId(1L);
        response.setFirstName("Admin");
        response.setLastName("User");
        response.setEmail("admin.user@example.com");
        response.setRoles(List.of(new RoleResponse()));

        when(userAdminService.getUsers()).thenReturn(List.of(response));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("admin.user@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserAsAdminShouldReturn200() throws Exception {
        UserAdminResponse response = new UserAdminResponse();
        response.setId(1L);
        response.setFirstName("Admin");
        response.setLastName("User");
        response.setEmail("admin.user@example.com");

        when(userAdminService.getUser(1L)).thenReturn(response);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin.user@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserAsAdminShouldReturn200() throws Exception {
        UserAdminUpdateRequest updateRequest = new UserAdminUpdateRequest();
        updateRequest.setFirstName("Admin");
        updateRequest.setLastName("User");
        updateRequest.setEmail("admin.user@example.com");
        updateRequest.setRoles(List.of(1L));
        updateRequest.setActive(true);

        UserAdminResponse response = new UserAdminResponse();
        response.setId(1L);
        response.setFirstName("Admin");
        response.setLastName("User");
        response.setEmail("admin.user@example.com");

        when(userAdminService.updateUser(any(Long.class), any(UserAdminUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUserAsAdminShouldReturn204() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }
}
