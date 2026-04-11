package com.vderperces.ecommerce.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vderperces.ecommerce.dto.user.ChangePasswordRequest;
import com.vderperces.ecommerce.dto.user.UserResponse;
import com.vderperces.ecommerce.dto.user.UserUpdateRequest;
import com.vderperces.ecommerce.exceptions.AuthException;
import com.vderperces.ecommerce.exceptions.ConflictException;
import com.vderperces.ecommerce.exceptions.NotFoundException;
import com.vderperces.ecommerce.mapper.UserMapper;
import com.vderperces.ecommerce.model.User;
import com.vderperces.ecommerce.repository.UserRepository;

/**
 * Service responsible for user profile management.
 */
@Service
public class UserService {

    /** Repository used to access user data storage. */
    private final UserRepository userRepository;

    /** Mapper used to convert users between entities and DTOs. */
    private final UserMapper userMapper;

    /** Password encoder used for hashing passwords. */
    private final PasswordEncoder passwordEncoder;

    public UserService(final UserRepository userRepository,
            final UserMapper userMapper,
            final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Update an existing user by email.
     *
     * @param username email address used as unique identifier
     * @param request  update request payload
     * @return user response after update
     */
    public UserResponse updateUser(final String username, final UserUpdateRequest request) {
        final User existingUser = this.userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + username));

        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        existingUser.setEmail(request.getEmail());

        try {
            final User updatedUser = this.userRepository.save(existingUser);
            return this.userMapper.toResponse(updatedUser);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Unable to update account. Provided information incorrect");
        }
    }

    /**
     * Change password for the user.
     *
     * @param username email identification
     * @param request  change password payload
     */
    public void changePassword(final String username, final ChangePasswordRequest request) {
        final User user = this.userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + username));

        if (!this.passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AuthException("Current password is incorrect", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(this.passwordEncoder.encode(request.getNewPassword()));
        this.userRepository.save(user);
    }

    /**
     * Get user by id.
     *
     * @param id user id
     * @return user response
     */
    public UserResponse getUser(final Long id) {
        final User user = this.userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return this.userMapper.toResponse(user);
    }

    /**
     * Get user by email.
     *
     * @param username email
     * @return user response
     */
    public UserResponse getUserByEmail(final String username) {
        final User user = this.userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + username));

        return this.userMapper.toResponse(user);
    }
}
