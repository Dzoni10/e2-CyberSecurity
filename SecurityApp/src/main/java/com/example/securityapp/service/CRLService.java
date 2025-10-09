package com.example.securityapp.service;

import com.example.securityapp.RepositoryInterfaces.CRLEntryRepositoryInterface;
import com.example.securityapp.domain.CRLEntry;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CRLService {

    private final CRLEntryRepositoryInterface crlEntryRepository;

    public CRLService(CRLEntryRepositoryInterface crlEntryRepository) {
        this.crlEntryRepository = crlEntryRepository;
    }

    public void addRevocation(Long certificateId, String serialNumber, String reason) {
        CRLEntry entry = new CRLEntry();
        entry.setCertificateId(certificateId);
        entry.setSerialNumber(serialNumber);
        entry.setRevokedAt(LocalDateTime.now());
        entry.setReason(reason);
        crlEntryRepository.save(entry);
    }

    public byte[] generateCRL() {
        List<CRLEntry> entries = crlEntryRepository.findAll();
        return entries.toString().getBytes(StandardCharsets.UTF_8);
    }

    public boolean isRevoked(String serialNumber) {
        return crlEntryRepository.existsBySerialNumber(serialNumber);
    }
}

