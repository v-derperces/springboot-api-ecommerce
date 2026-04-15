package com.vderperces.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vderperces.ecommerce.dto.user.ChangePasswordRequest;
import com.vderperces.ecommerce.exceptions.AuthException;
import com.vderperces.ecommerce.mapper.UserMapper;
import com.vderperces.ecommerce.model.User;
import com.vderperces.ecommerce.repository.UserRepository;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void changePasswordWrongCurrentPasswordShouldThrowAuthException() {
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
    void changePasswordCorrectCurrentPasswordShouldEncodeAndSave() {
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
