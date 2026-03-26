package fr.afpa.pompey.APIEcommerce.service;

import fr.afpa.pompey.APIEcommerce.dto.user.UserAdminCreateRequest;
import fr.afpa.pompey.APIEcommerce.dto.user.UserAdminResponse;
import fr.afpa.pompey.APIEcommerce.dto.user.UserAdminUpdateRequest;
import fr.afpa.pompey.APIEcommerce.exceptionhandler.CustomHttpException;
import fr.afpa.pompey.APIEcommerce.mapper.UserMapper;
import fr.afpa.pompey.APIEcommerce.model.Role;
import fr.afpa.pompey.APIEcommerce.model.User;
import fr.afpa.pompey.APIEcommerce.repository.OrderRepository;
import fr.afpa.pompey.APIEcommerce.repository.RoleRepository;
import fr.afpa.pompey.APIEcommerce.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAdminService {

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final RoleRepository roleRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    public UserAdminService(UserRepository userRepository, OrderRepository orderRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.roleRepository = null;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserAdminResponse> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toResponseAdmin).toList();
    }

    public List<User> getUsersByRole(String role) {
        return userRepository.findUsersByRole(role);
    }

    public UserAdminResponse getUser(Long id) throws CustomHttpException {
        User user = userRepository.findById(id).orElseThrow(() -> new CustomHttpException("User not found", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase()));
        return userMapper.toResponseAdmin(user);
    }

    public UserAdminResponse createUser(UserAdminCreateRequest request) throws CustomHttpException {
        try {
            User user = userMapper.toEntity(request);
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            List<Role> roles = roleRepository.findAllById(request.getRoles());
            user.setRoles(roles);

            User savedUser = userRepository.save(user);
            return userMapper.toResponseAdmin(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new CustomHttpException("Unable to create account. Please check the provided information.",
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase());
        }
    }

    public UserAdminResponse updateUser(Long id, UserAdminUpdateRequest request) throws CustomHttpException {
        User existingUser = userRepository.findById(id)
        .orElseThrow(() -> new CustomHttpException("User not found", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase()));

        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        existingUser.setEmail(request.getEmail());
        existingUser.setPhone(request.getPhone());
        existingUser.setAddress(request.getAddress());
        existingUser.setActive(request.isActive());

        List<Role> roles = roleRepository.findByRoleIdIn(request.getRoles());
        existingUser.setRoles(roles);

        try {
            User updatedUser = userRepository.save(existingUser);
            return userMapper.toResponseAdmin(updatedUser);
        } catch (DataIntegrityViolationException e) {
            throw new CustomHttpException("Unable to update account. Please check the provided information.",
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase());
        }
    }

    public void deleteUser(Long id) throws CustomHttpException {
        if (orderRepository.existsByUser_UserId(id)) {
            throw new CustomHttpException("The user has orders and cannot be deleted. You can deactivate them instead",
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase());
        }
        userRepository.deleteById(id);
    }
}
