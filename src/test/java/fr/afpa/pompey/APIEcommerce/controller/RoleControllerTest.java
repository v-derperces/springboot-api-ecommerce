package fr.afpa.pompey.APIEcommerce.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createRole_unauthenticated_shouldReturn401() throws Exception {
        String json = "{\"name\":\"TEST_ROLE\"}";

        mockMvc.perform(post("/roles")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createRole_asUser_shouldReturn403() throws Exception {
        String json = "{\"name\":\"TEST_ROLE\"}";

        mockMvc.perform(post("/roles")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRole_asAdmin_shouldReturn201() throws Exception {
        String json = "{\"name\":\"TEST_ROLE\"}";

        mockMvc.perform(post("/roles")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isCreated());
    }

    @Test
    void getRoles_unauthenticated_shouldReturn401() throws Exception {
        mockMvc.perform(get("/roles"))
        .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getRoles_asUser_shouldReturn403() throws Exception {
        mockMvc.perform(get("/roles"))
        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRoles_asAdmin_shouldReturn200() throws Exception {
        mockMvc.perform(get("/roles"))
        .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRole_asAdmin_shouldReturn200() throws Exception {
        String json = "{\"name\":\"UPDATED_ROLE\"}";


        mockMvc.perform(put("/roles/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRole_asAdmin_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/roles/1"))
        .andExpect(status().isNoContent());
    }

    @WithMockUser(roles = "ADMIN")
    @ParameterizedTest
    @ValueSource(strings = {"{\"name\":\"\"}", " \"{\"name\":\"      \"}\",{\"name\":\"A_VERY_LONG_ROLE_NAME_EXCEEDING_LIMITS\"}"})
    void createRole_invalidName_shouldReturn400() throws Exception {
        String json = "{\"name\":\"\"}";

        mockMvc.perform(post("/roles")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isBadRequest());
    }
}
