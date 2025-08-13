package com.example.securityapp.RepositoryInterfaces;

import com.example.securityapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepositoryInterface extends JpaRepository<User,Integer> {

    public User findUserByEmail(String email);
    public Optional<User> findByEmail(String email); // METODA ZA SPRING SECURITY KORISTI SE U CUSTOM USER DETAILS SERVICE
    public User findUserById(int id);
    public boolean existsByEmail(String email);
}
