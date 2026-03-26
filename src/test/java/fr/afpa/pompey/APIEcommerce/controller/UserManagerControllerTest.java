package fr.afpa.pompey.APIEcommerce.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.afpa.pompey.APIEcommerce.model.Role;
import fr.afpa.pompey.APIEcommerce.model.User;
import fr.afpa.pompey.APIEcommerce.repository.RoleRepository;
import fr.afpa.pompey.APIEcommerce.repository.UserRepository;
import fr.afpa.pompey.APIEcommerce.service.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserManagerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JWTService jwtService;

    private PasswordEncoder passwordEncoder;

    private Role roleUser, roleManager, roleAdmin;
    private User user, manager, admin;

    @BeforeEach
    void setUp() {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        roleUser = new Role();
        roleUser.setName("USER");
        roleRepository.save(roleUser);
        roleManager = new Role();
        roleManager.setName("MANAGER");
        roleRepository.save(roleManager);
        roleAdmin = new Role();
        roleAdmin.setName("ADMIN");
        roleRepository.save(roleAdmin);

        user = new User();
        user.setFirstName("Ron");
        user.setLastName("Weasley");
        user.setEmail("user@test.com");
        user.setPassword(passwordEncoder.encode("password")); user.setRoles(List.of(roleUser));
        userRepository.save(user);

        manager = new User();
        manager.setFirstName("Minerva");
        manager.setLastName("McGonagall");
        manager.setEmail("manager@test.com");
        manager.setPassword(passwordEncoder.encode("password"));
        manager.setRoles(List.of(roleManager));
        userRepository.save(manager);

        admin = new User();
        admin.setFirstName("Albus");
        admin.setLastName("Dumbledore");
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setRoles(List.of(roleAdmin));
        userRepository.save(admin);
    }

    private String loginAndGetToken(String email, String rawPassword) throws Exception {
        String loginJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, email, rawPassword);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode node = new ObjectMapper().readTree(response);
        return node.get("token").asText();
    }

    @Test
    void adminCanAccessAllUserEndpoints() throws Exception {
        String token = loginAndGetToken("admin@test.com", "password");

        mockMvc.perform(get("/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/user/" + user.getUserId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/user/" + manager.getUserId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/user/" + admin.getUserId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void managerCanAccessOnlyUserEndpoints() throws Exception {
        String token = loginAndGetToken("manager@test.com", "password");

        // Access to users is filtered by role USER
        mockMvc.perform(get("/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/user/" + user.getUserId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/user/" + manager.getUserId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/user/" + admin.getUserId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void userCannotAccessSensitiveEndpoints() throws Exception {
        String token = loginAndGetToken("user@test.com", "password");

        mockMvc.perform(get("/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/user/" + user.getUserId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/user/" + manager.getUserId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void managerCanUpdateUserButNotAdmin() throws Exception {
        String token = loginAndGetToken("manager@test.com", "password");
        String updateJson = """
                {
                    "firstName": "Updated",
                    "lastName": "User",
                    "email": "user@test.com",
                    "password": "newpass"
                }
                """;

        mockMvc.perform(put("/user/" + user.getUserId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());

        mockMvc.perform(put("/user/" + admin.getUserId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isForbidden());
    }
}
