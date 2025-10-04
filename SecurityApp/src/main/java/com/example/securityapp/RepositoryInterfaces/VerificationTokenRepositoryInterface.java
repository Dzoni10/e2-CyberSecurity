package com.example.securityapp.RepositoryInterfaces;

import com.example.securityapp.domain.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepositoryInterface extends JpaRepository<VerificationToken, Integer> {
    public VerificationToken findByToken(String token);
}
