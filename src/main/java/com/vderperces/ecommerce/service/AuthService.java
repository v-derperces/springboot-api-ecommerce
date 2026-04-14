package com.vderperces.ecommerce.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vderperces.ecommerce.dto.user.UserCreateRequest;
import com.vderperces.ecommerce.dto.user.UserResponse;
import com.vderperces.ecommerce.exceptions.AuthException;
import com.vderperces.ecommerce.exceptions.ConflictException;
import com.vderperces.ecommerce.mapper.UserMapper;
import com.vderperces.ecommerce.model.Role;
import com.vderperces.ecommerce.model.User;
import com.vderperces.ecommerce.repository.UserRepository;

/**
 * Service responsible for authentication and user registration operations.
 */
@Service
public class AuthService {

    /** Repository used to access user data storage. */
    private final UserRepository userRepository;

    /** Service for role-related operations. */
    private final RoleService roleService;

    /** Mapper used to convert users between entities and DTOs. */
    private final UserMapper userMapper;

    /** Password encoder used for hashing passwords. */
    private final PasswordEncoder passwordEncoder;

    /** Authentication manager for login operations. */
    private final AuthenticationManager authenticationManager;

    /** JWT service for token generation and validation. */
    private final JWTService jwtService;

    public AuthService(final UserRepository userRepository,
            final RoleService roleService,
            final UserMapper userMapper,
            final PasswordEncoder passwordEncoder,
            final AuthenticationManager authenticationManager,
            final JWTService jwtService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Register a new user.
     *
     * @param request sign-up request
     * @return user response after registration
     */
    public UserResponse register(final UserCreateRequest request) {
        try {
            final User user = this.userMapper.toEntity(request);
            user.setPassword(this.passwordEncoder.encode(request.getPassword()));
            final Role role = this.roleService.getRoleByName("USER");
            user.getRoles().add(role);

            final User savedUser = this.userRepository.save(user);
            return this.userMapper.toResponse(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Unable to create account. Please check the provided information.");
        }
    }

    /**
     * Authenticate and issue a JWT.
     *
     * @param username login username
     * @param password login password
     * @return JWT token string
     */
    public String login(final String username, final String password) {
        try {
            final Authentication authentication = this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            return this.jwtService.generateToken(authentication);
        } catch (AuthenticationException e) {
            throw new AuthException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }
}
