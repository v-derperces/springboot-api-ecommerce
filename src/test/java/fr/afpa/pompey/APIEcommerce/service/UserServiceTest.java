package fr.afpa.pompey.APIEcommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import fr.afpa.pompey.APIEcommerce.dto.user.ChangePasswordRequest;
import fr.afpa.pompey.APIEcommerce.dto.user.UserCreateRequest;
import fr.afpa.pompey.APIEcommerce.dto.user.UserResponse;
import fr.afpa.pompey.APIEcommerce.exceptions.AuthException;
import fr.afpa.pompey.APIEcommerce.exceptions.ConflictException;
import fr.afpa.pompey.APIEcommerce.mapper.UserMapper;
import fr.afpa.pompey.APIEcommerce.model.Role;
import fr.afpa.pompey.APIEcommerce.model.User;
import fr.afpa.pompey.APIEcommerce.repository.UserRepository;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_validUser_shouldReturnUserResponse() {
        UserCreateRequest request = new UserCreateRequest();
        request.setFirstName("Severus");
        request.setLastName("Rogue");
        request.setEmail("severus.rogue@slytherin.com");
        request.setPassword("password123");

        User user = new User();
        user.setFirstName("Severus");
        user.setLastName("Rogue");
        user.setEmail("severus.rogue@slytherin.com");

        Role userRole = new Role();
        userRole.setName("USER");

        User savedUser = new User();
        savedUser.setUserId(1L);
        savedUser.setFirstName("Severus");
        savedUser.setLastName("Rogue");
        savedUser.setEmail("severus.rogue@slytherin.com");
        savedUser.setPassword("encoded-pass");

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(1L);
        expectedResponse.setFirstName("Severus");
        expectedResponse.setLastName("Rogue");
        expectedResponse.setEmail("severus.rogue@slytherin.com");

        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-pass");
        when(roleService.getRoleByName("USER")).thenReturn(userRole);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        UserResponse result = userService.register(request);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void register_duplicateEmail_shouldThrowConflictException() {
        UserCreateRequest request = new UserCreateRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setEmail("jane.doe@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane.doe@example.com");

        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-pass");
        when(roleService.getRoleByName("USER")).thenReturn(new Role());
        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(ConflictException.class, () -> userService.register(request));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void changePassword_wrongCurrentPassword_shouldThrowAuthException() {
        String email = "verify@example.com";

        User existingUser = new User();
        existingUser.setEmail(email);
        existingUser.setPassword("encoded-old");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("bad-password");
        request.setNewPassword("new-password");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("bad-password", "encoded-old")).thenReturn(false);

        assertThrows(AuthException.class, () -> userService.changePassword(email, request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_correctCurrentPassword_shouldEncodeAndSave() {
        String email = "verify@example.com";

        User existingUser = new User();
        existingUser.setEmail(email);
        existingUser.setPassword("encoded-old");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("old-password");
        request.setNewPassword("new-password");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("old-password", "encoded-old")).thenReturn(true);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new");

        userService.changePassword(email, request);

        assertEquals("encoded-new", existingUser.getPassword());
        verify(userRepository, times(1)).save(existingUser);
    }
}
