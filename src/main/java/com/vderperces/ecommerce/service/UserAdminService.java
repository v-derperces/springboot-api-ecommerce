package com.vderperces.ecommerce.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

/**
 * Service for admin-level user management flows.
 */
@Service
public class UserAdminService {

    /** Repository to read/write users. */
    private final UserRepository userRepository;

    /** Repository to read orders related to users. */
    private final OrderRepository orderRepository;

    /** Repository to read role details. */
    private final RoleRepository roleRepository;

    /** Mapper for converting between admin user DTOs and User entities. */
    private final UserMapper userMapper;

    /** Encoder for handling user passwords. */
    private final PasswordEncoder passwordEncoder;

    public UserAdminService(final UserRepository userRepository, final OrderRepository orderRepository,
            final UserMapper userMapper, final PasswordEncoder passwordEncoder, final RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Get all admin users.
     *
     * @return list of user admin responses
     */
    public List<UserAdminResponse> getUsers() {
        return this.userRepository.findAll().stream().map(this.userMapper::toResponseAdmin).toList();
    }

    /**
     * Get users by role name.
     *
     * @param role role name
     * @return list of users
     */
    public List<User> getUsersByRole(final String role) {
        return this.userRepository.findUsersByRole(role);
    }

    /**
     * Get a user by id.
     *
     * @param id user id
     * @return user admin response
     */
    public UserAdminResponse getUser(final Long id) {
        final User user = this.userRepository.findById(id).orElseThrow(() -> new NotFoundException(
                "User not found with id: " + id));
        return this.userMapper.toResponseAdmin(user);
    }

    /**
     * Create a user (admin-level).
     *
     * @param request user create request
     * @return created user admin response
     */
    public UserAdminResponse createUser(final UserAdminCreateRequest request) {
        try {
            final User user = this.userMapper.toEntity(request);
            user.setPassword(this.passwordEncoder.encode(request.getPassword()));

            final List<Role> roles = this.roleRepository.findAllById(request.getRoles());
            user.setRoles(roles);

            final User savedUser = this.userRepository.save(user);
            return this.userMapper.toResponseAdmin(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Unable to create account. Provided information is incorrect");
        }
    }

    /**
     * Update a user.
     *
     * @param id      user id
     * @param request user update request
     * @return updated user admin response
     */
    public UserAdminResponse updateUser(final Long id, final UserAdminUpdateRequest request) {
        final User existingUser = this.userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        existingUser.setEmail(request.getEmail());
        existingUser.setPhone(request.getPhone());
        existingUser.setAddress(request.getAddress());
        existingUser.setActive(request.isActive());

        final List<Role> roles = this.roleRepository.findByRoleIdIn(request.getRoles());
        existingUser.setRoles(roles);

        try {
            final User updatedUser = this.userRepository.save(existingUser);
            return this.userMapper.toResponseAdmin(updatedUser);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Unable to update account. Provided information is incorrect");
        }
    }

    /**
     * Delete a user by id.
     *
     * @param id user id
     */
    public void deleteUser(final Long id) {
        if (this.orderRepository.existsByUser_UserId(id)) {
            throw new ConflictException("User has associated orders. Deactivate instead of deleting");
        }
        this.userRepository.deleteById(id);
    }
}
