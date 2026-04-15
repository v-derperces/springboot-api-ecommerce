package com.vderperces.ecommerce.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.vderperces.ecommerce.dto.role.RoleRequest;
import com.vderperces.ecommerce.dto.role.RoleResponse;
import com.vderperces.ecommerce.exceptions.ConflictException;
import com.vderperces.ecommerce.exceptions.NotFoundException;
import com.vderperces.ecommerce.mapper.RoleMapper;
import com.vderperces.ecommerce.model.Role;
import com.vderperces.ecommerce.repository.RoleRepository;

/**
 * Service responsible for role business logic and persistence operations.
 */
@Service
public class RoleService {

    /** Repository used to access role data storage. */
    private final RoleRepository roleRepository;

    /** Mapper used to convert between role entities and DTOs. */
    private final RoleMapper roleMapper;

    public RoleService(final RoleRepository roleRepository, final RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    /**
     * Get all roles.
     *
     * @return list of role responses
     */
    public List<RoleResponse> getRoles() {
        return this.roleRepository.findAll().stream().map(this.roleMapper::toDTO).toList();
    }

    /**
     * Get role by id.
     *
     * @param id role id
     * @return role response
     */
    public RoleResponse getRole(final Long id) {
        final Role role = this.roleRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Cannot get role: No role found with id: " + id));

        return this.roleMapper.toDTO(role);
    }

    /**
     * Create a role.
     *
     * @param request role request payload
     * @return created role response
     */
    public RoleResponse createRole(final RoleRequest request) {
        try {
            final Role role = this.roleMapper.toEntity(request);
            return this.roleMapper.toDTO(this.roleRepository.save(role));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(
                    "Cannot create role: a role with name '" + request.getName() + "' already exists");
        }
    }

    /**
     * Update a role.
     *
     * @param id      role id
     * @param request role request payload
     * @return updated role response
     */
    public RoleResponse updateRole(final Long id, final RoleRequest request) {
        final Role existingRole = this.roleRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Cannot update role: No role found with id: " + id));
        try {
            existingRole.setName(request.getName());
            return this.roleMapper.toDTO(this.roleRepository.save(existingRole));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(
                    "Cannot update role: a role with name '" + request.getName() + "' already exists");
        }
    }

    /**
     * Delete role by id.
     *
     * @param id role id
     */
    public void deleteRole(final Long id) {
        try {
            this.roleRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(
                    "Role with id " + id + " cannot be delete because it is associated with a user.");
        }
    }

    /**
     * Get role by name.
     *
     * @param name role name
     * @return role entity
     */
    public Role getRoleByName(final String name) {
        return this.roleRepository.findByName(name).orElseThrow(
                () -> new NotFoundException("Cannot get role: No role found with name: " + name));
    }

}
