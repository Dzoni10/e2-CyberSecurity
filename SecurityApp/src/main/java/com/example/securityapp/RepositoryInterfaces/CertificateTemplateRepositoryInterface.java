package com.example.securityapp.RepositoryInterfaces;

import com.example.securityapp.domain.CertificateTemplate;
import com.example.securityapp.dto.CertificateTemplateDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateTemplateRepositoryInterface extends JpaRepository<CertificateTemplate, Long> {
    List<CertificateTemplate> findByOwnerId(Long ownerId);

    List<CertificateTemplate> findByIssuerId(Integer issuerId);
}

