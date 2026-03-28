package fr.afpa.pompey.APIEcommerce.service;

import fr.afpa.pompey.APIEcommerce.dto.user.ChangePasswordRequest;
import fr.afpa.pompey.APIEcommerce.dto.user.UserCreateRequest;
import fr.afpa.pompey.APIEcommerce.dto.user.UserResponse;
import fr.afpa.pompey.APIEcommerce.dto.user.UserUpdateRequest;
import fr.afpa.pompey.APIEcommerce.exceptions.AuthException;
import fr.afpa.pompey.APIEcommerce.exceptions.ConflictException;
import fr.afpa.pompey.APIEcommerce.exceptions.NotFoundException;
import fr.afpa.pompey.APIEcommerce.mapper.UserMapper;
import fr.afpa.pompey.APIEcommerce.model.Role;
import fr.afpa.pompey.APIEcommerce.model.User;
import fr.afpa.pompey.APIEcommerce.repository.UserRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JWTService jwtService;

    public UserService(UserRepository userRepository, RoleService roleService, UserMapper userMapper, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserResponse register(UserCreateRequest request) {
        try {
            User user = userMapper.toEntity(request);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            Role role = roleService.getRoleByName("USER");
            user.getRoles().add(role);

            User savedUser = userRepository.save(user);
            return userMapper.toResponse(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Unable to create account. Please check the provided information.");
        }
    }

    public String login(String username, String password) {
        try {
            Authentication authentication =
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            String token = jwtService.generateToken(authentication);
            return token;
        } catch (AuthenticationException e) {
            throw new AuthException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    public UserResponse updateUser(String username, UserUpdateRequest request) {
        User existingUser = userRepository.findByEmail(username)
        .orElseThrow(() -> new NotFoundException("User not found with email: " + username));

        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        existingUser.setEmail(request.getEmail());

        try {
            User updatedUser = userRepository.save(existingUser);
            return userMapper.toResponse(updatedUser);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Unable to update account. Provided information incorrect");
        }
    }

    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new NotFoundException("User not found with email: " + username));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AuthException("Current password is incorrect", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    public UserResponse getUserByEmail(String username) {
        User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new NotFoundException("User not found with email: " + username));

        return userMapper.toResponse(user);
    }
}
