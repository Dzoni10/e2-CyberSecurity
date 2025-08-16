package com.example.securityapp.RepositoryInterfaces;

import com.example.securityapp.domain.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepositoryInterface extends JpaRepository<Certificate, Integer> {
}
