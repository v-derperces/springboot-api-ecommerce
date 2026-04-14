package com.vderperces.ecommerce.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.vderperces.ecommerce.model.User;
import com.vderperces.ecommerce.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.getUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isActive(),
                true,
                true,
                true,
                user.getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role
                                .getName()))
                        .toList());
    }

}
