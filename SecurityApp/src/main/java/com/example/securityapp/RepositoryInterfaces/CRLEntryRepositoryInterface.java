package com.example.securityapp.RepositoryInterfaces;

import com.example.securityapp.domain.CRLEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CRLEntryRepositoryInterface extends JpaRepository<CRLEntry, Long> {
    boolean existsBySerialNumber(String serialNumber);
    List<CRLEntry> findAll();
}

