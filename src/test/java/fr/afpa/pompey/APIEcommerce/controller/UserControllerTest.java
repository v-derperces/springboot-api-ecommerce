package fr.afpa.pompey.APIEcommerce.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.afpa.pompey.APIEcommerce.model.Role;
import fr.afpa.pompey.APIEcommerce.model.User;
import fr.afpa.pompey.APIEcommerce.repository.RoleRepository;
import fr.afpa.pompey.APIEcommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;
    private Role roleUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        roleUser = new Role();
        roleUser.setName("USER");
        roleRepository.save(roleUser);

        user = new User();
        user.setFirstName("Ron");
        user.setLastName("Weasley");
        user.setEmail("ron@hogwarts.com");
        user.setRoles(new ArrayList<>(List.of(roleUser)));
        user.setPassword(passwordEncoder.encode("Babbity*Rabbity"));
        userRepository.save(user);
    }

    @Test
    void userNotAuthentificatedAccessDenied() throws Exception {
        mockMvc.perform(get("/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userWithUserRoleCanAccessMeEndpoint() throws Exception {

        String loginJson = """
        {
            "username": "ron@hogwarts.com",
            "password": "Babbity*Rabbity"
        }
        """;
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response);
        String token = node.get("token").asText();

        mockMvc.perform(get("/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}