package com.vderperces.ecommerce.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createRoleUnauthenticatedShouldReturn401() throws Exception {
        String json = "{\"name\":\"TEST_ROLE_UNAUTHENTICATED\"}";

        mockMvc.perform(post("/api/v1/admin/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createRoleAsUserShouldReturn403() throws Exception {
        String json = "{\"name\":\"TEST_ROLE_AS_USER\"}";

        mockMvc.perform(post("/api/v1/admin/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRoleAsAdminShouldReturn201() throws Exception {
        String json = "{\"name\":\"TEST_ROLE_ADMIN\"}";

        mockMvc.perform(post("/api/v1/admin/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void getRolesUnauthenticatedShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/admin/roles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getRolesAsUserShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/roles"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRolesAsAdminShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/admin/roles"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRoleAsAdminShouldReturn200() throws Exception {
        String json = "{\"name\":\"UPDATED_ROLE\"}";

        mockMvc.perform(put("/api/v1/admin/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRoleAsAdminShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/roles/1"))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(roles = "ADMIN")
    @ParameterizedTest
    @ValueSource(strings = { "{\"name\":\"\"}", "{\"name\":\"      \"}",
            "{\"name\":\"A_VERY_VERY_LONG_ROLE_NAME_EXCEEDING_LIMITS\"}" })
    void createRoleInvalidNameShouldReturn400(String json) throws Exception {
        mockMvc.perform(post("/api/v1/admin/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }
}
