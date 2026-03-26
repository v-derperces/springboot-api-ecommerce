package fr.afpa.pompey.APIEcommerce.controller;

import fr.afpa.pompey.APIEcommerce.model.User;
import fr.afpa.pompey.APIEcommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthentificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        String json = """
            {
                "firstName": "Harry",
                "lastName": "Potter",
                "email": "hp@hoghwarts.com",
                "password": "9-3/4"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void testRegisterMissingPassword() throws Exception {
        String json = """
            {
                "firstName": "Hermione",
                "lastName": "Granger",
                "email": "hermione@hogwarts.com"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterDuplicateEmail() throws Exception {
        User user = new User();
        user.setFirstName("Ron");
        user.setLastName("Weasley");
        user.setEmail("ron@hogwarts.com");
        user.setPassword("Babbity*Rabbity");

        userRepository.save(user); // Simulates existing user

        String json = """
            {
                "firstName": "Ron",
                "lastName": "Weasley",
                "email": "ron@hogwarts.com",
                "password": "Babbity*Rabbity"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    void testLoginSuccess() throws Exception {
        String json = """
            {
                "firstName": "Harry",
                "lastName": "Potter",
                "email": "hp@hoghwarts.com",
                "password": "9-3/4"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        String loginJson = """
            {
                "username": "hp@hoghwarts.com",
                "password": "9-3/4"
            }
            """;
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginInvalidCredentials() throws Exception {
        String json = """
            {
                "firstName": "Harry",
                "lastName": "Potter",
                "email": "hp@hoghwarts.com",
                "password": "9-3/4"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        String loginJson = """
            {
                "email": "hp@hoghwarts.com",
                "password": "9-1/4"
            }
            """;
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized());
    }

}
