package com.example.securityapp.RepositoryInterfaces;

import com.example.securityapp.domain.CertificateSigningRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CSRRepositoryInterface extends JpaRepository<CertificateSigningRequest,Long> {
}
