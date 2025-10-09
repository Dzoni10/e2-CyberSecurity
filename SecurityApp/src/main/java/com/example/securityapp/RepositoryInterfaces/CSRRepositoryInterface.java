package com.example.securityapp.RepositoryInterfaces;

import com.example.securityapp.domain.CSRStatus;
import com.example.securityapp.domain.CertificateSigningRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CSRRepositoryInterface extends JpaRepository<CertificateSigningRequest,Long> {

    @Query("SELECT c FROM CertificateSigningRequest c WHERE c.selectedCaId IN :caIds AND c.status = :status")
    List<CertificateSigningRequest> findBySelectedCaIdInAndStatus(
            @Param("caIds") List<Long> caIds,
            @Param("status") CSRStatus status);

}
