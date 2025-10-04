package com.example.securityapp.controller;

import com.example.securityapp.dto.CertificateRequestDTO;
import com.example.securityapp.dto.CertificateResponseDTO;
import com.example.securityapp.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @GetMapping(value="/all")
    public ResponseEntity<List<CertificateResponseDTO>> getAll() {
        return ResponseEntity.ok(certificateService.getAllCertificates());
    }

    @GetMapping(value="/ca")
    public ResponseEntity<List<CertificateResponseDTO>> getAllCACertificates() {
        return ResponseEntity.ok(certificateService.getAllCACertificates());
    }

    @PostMapping(value="/issue")
    public CertificateResponseDTO issueCertificate(@RequestBody CertificateRequestDTO request){
        return certificateService.issueCertificate(request);
    }

    @PutMapping("/{id}/revoke")
    public void revokeCertificate(@PathVariable int id){
        certificateService.revokeCertificate(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificateResponseDTO> getById(@PathVariable int id) {
        return ResponseEntity.ok(certificateService.getCertificateById(id));
    }

    @GetMapping("/verify/{id}")
    public ResponseEntity<String> verifyCertificate(@PathVariable int id) {
        try {
            certificateService.verifyCertificateChain(id);
            return ResponseEntity.ok("Certificate chain verification completed. Check server logs.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Verification failed: " + e.getMessage());
        }
    }

}
