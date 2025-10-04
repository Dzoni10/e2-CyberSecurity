package com.example.securityapp.service;

import com.example.securityapp.RepositoryInterfaces.VerificationTokenRepositoryInterface;
import com.example.securityapp.domain.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenService {

    private final VerificationTokenRepositoryInterface verificationTokenRepository;

    @Autowired
    public VerificationTokenService(VerificationTokenRepositoryInterface verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public VerificationToken findByToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    public VerificationToken save(VerificationToken verificationToken){
        return verificationTokenRepository.save(verificationToken);
    }


}
