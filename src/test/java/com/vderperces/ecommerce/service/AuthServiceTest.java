package com.vderperces.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vderperces.ecommerce.dto.user.UserCreateRequest;
import com.vderperces.ecommerce.dto.user.UserResponse;
import com.vderperces.ecommerce.exceptions.ConflictException;
import com.vderperces.ecommerce.mapper.UserMapper;
import com.vderperces.ecommerce.model.Role;
import com.vderperces.ecommerce.model.User;
import com.vderperces.ecommerce.repository.UserRepository;

class AuthServiceTest {

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
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerValidUserShouldReturnUserResponse() {
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

        UserResponse result = authService.register(request);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void registerDuplicateEmailShouldThrowConflictException() {
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

        assertThrows(ConflictException.class, () -> authService.register(request));

        verify(userRepository, times(1)).save(any(User.class));
    }
}
