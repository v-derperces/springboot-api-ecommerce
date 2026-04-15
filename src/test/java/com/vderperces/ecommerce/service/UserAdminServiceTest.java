package com.vderperces.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vderperces.ecommerce.dto.user.UserAdminCreateRequest;
import com.vderperces.ecommerce.dto.user.UserAdminResponse;
import com.vderperces.ecommerce.dto.user.UserAdminUpdateRequest;
import com.vderperces.ecommerce.exceptions.ConflictException;
import com.vderperces.ecommerce.exceptions.NotFoundException;
import com.vderperces.ecommerce.mapper.UserMapper;
import com.vderperces.ecommerce.model.Role;
import com.vderperces.ecommerce.model.User;
import com.vderperces.ecommerce.repository.OrderRepository;
import com.vderperces.ecommerce.repository.RoleRepository;
import com.vderperces.ecommerce.repository.UserRepository;

class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAdminService userAdminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserNotFoundShouldThrowNotFoundException() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userAdminService.getUser(42L));
    }

    @Test
    void createUserValidInputShouldReturnMappedResponse() {
        UserAdminCreateRequest request = new UserAdminCreateRequest();
        request.setFirstName("New");
        request.setLastName("User");
        request.setEmail("new.user@example.com");
        request.setPassword("password123");
        request.setRoles(List.of(1L, 2L));

        Role r1 = new Role();
        r1.setRoleId(1L);
        Role r2 = new Role();
        r2.setRoleId(2L);

        User incoming = new User();
        User saved = new User();
        saved.setUserId(99L);

        UserAdminResponse response = new UserAdminResponse();
        response.setId(99L);

        when(userMapper.toEntity(request)).thenReturn(incoming);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-pass");
        when(roleRepository.findAllById(request.getRoles())).thenReturn(List.of(r1, r2));
        when(userRepository.save(incoming)).thenReturn(saved);
        when(userMapper.toResponseAdmin(saved)).thenReturn(response);

        UserAdminResponse result = userAdminService.createUser(request);

        assertNotNull(result);
        assertEquals(99L, result.getId());
        verify(userRepository).save(incoming);
    }

    @Test
    void createUserDuplicateEmailShouldThrowConflictException() {
        UserAdminCreateRequest request = new UserAdminCreateRequest();
        request.setFirstName("New");
        request.setLastName("User");
        request.setEmail("duplicate@example.com");
        request.setPassword("password123");
        request.setRoles(List.of(1L));

        User incoming = new User();

        when(userMapper.toEntity(request)).thenReturn(incoming);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-pass");
        when(roleRepository.findAllById(request.getRoles())).thenReturn(List.of(new Role()));
        when(userRepository.save(incoming)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(ConflictException.class, () -> userAdminService.createUser(request));
    }

    @Test
    void updateUserNotFoundShouldThrowNotFoundException() {
        when(userRepository.findById(123L)).thenReturn(Optional.empty());

        UserAdminUpdateRequest request = new UserAdminUpdateRequest();
        request.setFirstName("Nombre");
        request.setLastName("Apellido");
        request.setEmail("fake@example.com");
        request.setRoles(List.of(1L));
        request.setActive(true);

        assertThrows(NotFoundException.class, () -> userAdminService.updateUser(123L, request));
    }

    @Test
    void updateUserValidInputShouldReturnMappedResponse() {
        User existing = new User();
        existing.setUserId(2L);

        UserAdminUpdateRequest request = new UserAdminUpdateRequest();
        request.setFirstName("Updated");
        request.setLastName("Name");
        request.setEmail("updated@example.com");
        request.setRoles(List.of(1L));
        request.setActive(false);

        Role role = new Role();
        role.setRoleId(1L);

        User updated = new User();
        updated.setUserId(2L);

        UserAdminResponse response = new UserAdminResponse();
        response.setId(2L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(roleRepository.findByRoleIdIn(request.getRoles())).thenReturn(List.of(role));
        when(userRepository.save(existing)).thenReturn(updated);
        when(userMapper.toResponseAdmin(updated)).thenReturn(response);

        UserAdminResponse result = userAdminService.updateUser(2L, request);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        verify(userRepository).save(existing);
    }

    @Test
    void deleteUserWithOrdersShouldThrowConflictException() {
        when(orderRepository.existsByUser_UserId(5L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> userAdminService.deleteUser(5L));
    }

    @Test
    void deleteUserWithoutOrdersShouldDeleteUser() {
        when(orderRepository.existsByUser_UserId(10L)).thenReturn(false);
        doNothing().when(userRepository).deleteById(10L);

        userAdminService.deleteUser(10L);

        verify(userRepository).deleteById(10L);
    }
}
