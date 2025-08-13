package com.example.securityapp.service;

import com.example.securityapp.RepositoryInterfaces.UserRepositoryInterface;
import com.example.securityapp.config.CustomUserDetails;
import com.example.securityapp.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService  implements UserDetailsService {

    private final UserRepositoryInterface userRepository;

    public CustomUserDetailsService(UserRepositoryInterface userRepositoryInterface) {
        this.userRepository = userRepositoryInterface;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }
}
