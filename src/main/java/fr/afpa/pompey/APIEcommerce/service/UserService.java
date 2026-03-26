package fr.afpa.pompey.APIEcommerce.service;

import fr.afpa.pompey.APIEcommerce.dto.user.ChangePasswordRequest;
import fr.afpa.pompey.APIEcommerce.dto.user.UserCreateRequest;
import fr.afpa.pompey.APIEcommerce.dto.user.UserResponse;
import fr.afpa.pompey.APIEcommerce.dto.user.UserUpdateRequest;
import fr.afpa.pompey.APIEcommerce.exceptionhandler.CustomHttpException;
import fr.afpa.pompey.APIEcommerce.mapper.UserMapper;
import fr.afpa.pompey.APIEcommerce.model.Role;
import fr.afpa.pompey.APIEcommerce.model.User;
import fr.afpa.pompey.APIEcommerce.repository.RoleRepository;
import fr.afpa.pompey.APIEcommerce.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse getUser(Long id) throws CustomHttpException {
        User user = userRepository.findById(id).orElseThrow(() -> new CustomHttpException("User not found", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase()));
        return userMapper.toResponse(user);
    }

    public UserResponse createUser(UserCreateRequest request) throws CustomHttpException {
        try {
            User user = userMapper.toEntity(request);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            Role role = roleRepository.findByName("USER").orElseThrow(() -> new CustomHttpException("Default role not found", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
            user.getRoles().add(role);

            User savedUser = userRepository.save(user);
            return userMapper.toResponse(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new CustomHttpException("Unable to create account. Please check the provided information.",
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase());
        }
    }

    public UserResponse updateUser(String username, UserUpdateRequest request) throws CustomHttpException {
        User existingUser = userRepository.findByEmail(username)
        .orElseThrow(() -> new CustomHttpException("User not found", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase()));

        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        existingUser.setEmail(request.getEmail());

        try {
            User updatedUser = userRepository.save(existingUser);
            return userMapper.toResponse(updatedUser);
        } catch (DataIntegrityViolationException e) {
            throw new CustomHttpException("Unable to update account. Please check the provided information.",
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase());
        }
    }

    public UserResponse getUserByEmail(String username) throws CustomHttpException {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new CustomHttpException("User not found", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase()));

        return userMapper.toResponse(user);
    }

    public String changePassword(String username, ChangePasswordRequest request) throws CustomHttpException {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new CustomHttpException("User not found", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase()));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomHttpException("Current password is incorrect", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase());
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return "Password changed successfully";
    }
}
