package com.vderperces.ecommerce.integration;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vderperces.ecommerce.dto.LoginRequest;
import com.vderperces.ecommerce.dto.user.UserCreateRequest;
import com.vderperces.ecommerce.model.Role;
import com.vderperces.ecommerce.model.User;
import com.vderperces.ecommerce.repository.RoleRepository;
import com.vderperces.ecommerce.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

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
    }

    @Test
    void registerShouldCreateUserValid() throws Exception {
        String email = "ron.weasly@hogwarts.com";

        UserCreateRequest request = new UserCreateRequest();
        request.setFirstName("Ron");
        request.setLastName("Weasly");
        request.setEmail(email);
        request.setPassword("TestPass123");

        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.firstName").value("Ron"))
                .andExpect(jsonPath("$.lastName").value("Weasly"));

        User saved = userRepository.findByEmail(email).orElseThrow();

        assert saved.getFirstName().equals("Ron");
        assert saved.getLastName().equals("Weasly");
    }

    @Test
    void registerDuplicateEmailShouldReturnConflict() throws Exception {
        Role userRole = roleRepository.findByName("USER").orElseThrow();

        User existing = new User();
        existing.setFirstName("Hermione");
        existing.setLastName("Granger");
        existing.setEmail(EMAIL);
        existing.setPassword(passwordEncoder.encode(PASSWORD));
        existing.setRoles(List.of(userRole));
        existing.setActive(true);
        userRepository.save(existing);

        UserCreateRequest request = new UserCreateRequest();
        request.setFirstName("Hermione");
        request.setLastName("Granger");
        request.setEmail(EMAIL);
        request.setPassword(PASSWORD);

        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Unable to create account. Please check the provided information."));
    }

    @Test
    void loginWithValidCredentialsShouldReturnToken() throws Exception {
        createUser(true, EMAIL, PASSWORD);

        LoginRequest login = new LoginRequest();
        login.setUsername(EMAIL);
        login.setPassword(PASSWORD);

        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login))).andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void loginWithInvalidCredentialsShouldReturnUnauthorized() throws Exception {
        createUser(true, EMAIL, PASSWORD);

        LoginRequest login = new LoginRequest();
        login.setUsername("wrong@hogwarts.com");
        login.setPassword("wrongpass");

        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void loginWithInactiveUserShouldReturnUnauthorized() throws Exception {
        createUser(false, EMAIL, PASSWORD);

        LoginRequest login = new LoginRequest();
        login.setUsername(EMAIL);
        login.setPassword(PASSWORD);

        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    private User createUser(boolean active, String email, String rawPassword) {
        Role userRole = roleRepository.findByName("USER").orElseThrow();

        User user = new User();
        user.setFirstName("Harry");
        user.setLastName("Potter");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setActive(active);
        user.setRoles(List.of(userRole));

        return userRepository.save(user);
    }
}
