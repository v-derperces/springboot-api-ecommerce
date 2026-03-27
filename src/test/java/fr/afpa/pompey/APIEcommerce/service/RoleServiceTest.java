package fr.afpa.pompey.APIEcommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

import fr.afpa.pompey.APIEcommerce.dto.role.RoleRequest;
import fr.afpa.pompey.APIEcommerce.dto.role.RoleResponse;
import fr.afpa.pompey.APIEcommerce.exceptions.ConflictException;
import fr.afpa.pompey.APIEcommerce.exceptions.NotFoundException;
import fr.afpa.pompey.APIEcommerce.mapper.RoleMapper;
import fr.afpa.pompey.APIEcommerce.model.Role;
import fr.afpa.pompey.APIEcommerce.repository.RoleRepository;

public class RoleServiceTest {
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRoleById_notFound_shouldThrowRoleNotFoundException() {
        long roleId = 999;
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
            NotFoundException.class,
            () -> roleService.getRole(roleId)
        );
        assertTrue(exception.getMessage().contains("No role found with id: 999"));    }

        @Test
        void createRole_valid_shouldSaveRole() {
            RoleRequest request = new RoleRequest();
            request.setName("ADMIN");

            Role role = new Role();
            role.setName("ADMIN");

            Role savedRole = new Role();
            savedRole.setRoleId(1L);
            savedRole.setName("ADMIN");

            RoleResponse response = new RoleResponse();
            response.setName("ADMIN");

            when(roleMapper.toEntity(request)).thenReturn(role);
            when(roleRepository.save(role)).thenReturn(savedRole);
            when(roleMapper.toDTO(savedRole)).thenReturn(response);

            RoleResponse result = roleService.createRole(request);

            assertNotNull(result);
            assertEquals("ADMIN", result.getName());

            verify(roleRepository).save(role);
        }

        @Test
        void createRole_duplicateName_shouldThrowConflictException() {
            RoleRequest request = new RoleRequest();
            request.setName("ADMIN");

            Role role = new Role();
            role.setName("ADMIN");

            when(roleMapper.toEntity(request)).thenReturn(role);

            when(roleRepository.save(any(Role.class)))
            .thenThrow(DataIntegrityViolationException.class);

            ConflictException exception = assertThrows(
                ConflictException.class,
                () -> roleService.createRole(request)
            );

            assertTrue(exception.getMessage().contains("already exists"));

            verify(roleRepository, times(1)).save(any(Role.class));
        }
    }
