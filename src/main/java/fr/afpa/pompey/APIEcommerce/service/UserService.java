package fr.afpa.pompey.APIEcommerce.service;

import fr.afpa.pompey.APIEcommerce.exceptionhandler.CustomHttpException;
import fr.afpa.pompey.APIEcommerce.model.User;
import fr.afpa.pompey.APIEcommerce.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    public Iterable<User> getUsersByRole(String role) {
        return userRepository.findUsersByRole(role);
    }

    public Optional<User> getUser(int id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) throws CustomHttpException {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new CustomHttpException("Unable to create account. Please check the provided information.",
                    HttpStatus.CONFLICT.value(),
                    HttpStatus.CONFLICT.getReasonPhrase());
        }
    }

    public void deleteUser(int id) throws CustomHttpException {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User u = user.get();
            if (!u.getOrders().isEmpty()) {
                throw new CustomHttpException("The user has orders and cannot be deleted. You can deactivate them instead",
                            HttpStatus.CONFLICT.value(),
                            HttpStatus.CONFLICT.getReasonPhrase());
            }
            userRepository.deleteById(id);
        }
    }

    public Optional<User> getUserByEmail(String username) {
        return userRepository.findByEmail(username);
    }

}
