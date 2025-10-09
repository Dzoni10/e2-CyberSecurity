package com.example.securityapp.controller;

import com.example.securityapp.service.CRLService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/revocation")
public class RevocationController {

    private final CRLService crlService;

    public RevocationController(CRLService crlService) {
        this.crlService = crlService;
    }

    @GetMapping("/crl")
    public ResponseEntity<byte[]> getCRL() {
        byte[] crlBytes = crlService.generateCRL();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pkix-crl")
                .body(crlBytes);
    }

    @GetMapping("/ocsp")
    public ResponseEntity<String> checkOCSP(@RequestParam String serialNumber) {
        boolean revoked = crlService.isRevoked(serialNumber);
        return ResponseEntity.ok(revoked ? "revoked" : "good");
    }
}

