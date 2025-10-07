package com.example.securityapp.controller;

import com.example.securityapp.dto.CertificateRequestDTO;
import com.example.securityapp.dto.CertificateResponseDTO;
import com.example.securityapp.service.CertificateService;
import com.example.securityapp.service.CustomLoggerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@Validated
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CustomLoggerService loggerService;

    @GetMapping(value="/all")
    public ResponseEntity<List<CertificateResponseDTO>> getAll(HttpServletRequest request) {
        return ResponseEntity.ok(certificateService.getAllCertificates());
    }

    @GetMapping(value="/ca")
    public ResponseEntity<List<CertificateResponseDTO>> getAllCACertificates(HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        String user = getCurrentUser();
        String role = getCurrentUserRole();

        loggerService.logCertificateEvent(
                "CA_CERTIFICATE_LIST_ACCESSED",
                user,
                role,
                "SUCCESS",
                "Retrieved all CA certificates",
                ipAddress,
                "N/A",
                "N/A",
                "N/A"
        );

        return ResponseEntity.ok(certificateService.getAllCACertificates());
    }

    @PostMapping(value="/issue")
    public CertificateResponseDTO issueCertificate(
            @Valid @RequestBody CertificateRequestDTO request,
            HttpServletRequest httpRequest){
        String ipAddress = getClientIpAddress(httpRequest);
        String user = getCurrentUser();
        String role = getCurrentUserRole();

        try {
            CertificateResponseDTO response = certificateService.issueCertificate(request);

            String issuerInfo = request.issuerId != null ? "Issuer ID: " + request.issuerId : "SELF_SIGNED";

            loggerService.logCertificateEvent(
                    "CERTIFICATE_ISSUED",
                    user,
                    role,
                    "SUCCESS",
                    "Certificate issued successfully",
                    ipAddress,
                    String.valueOf(response.getId()),
                    request.cn,
                    issuerInfo
            );

            return response;
        } catch (Exception e) {
            String issuerInfo = request.issuerId != null ? "Issuer ID: " + request.issuerId : "SELF_SIGNED";

            loggerService.logCertificateEvent(
                    "CERTIFICATE_ISSUE_FAILED",
                    user,
                    role,
                    "FAILURE",
                    "Failed to issue certificate: " + e.getMessage(),
                    ipAddress,
                    "N/A",
                    request.cn,
                    issuerInfo
            );
            throw e;
        }
    }

    @PutMapping("/{id}/revoke")
    public void revokeCertificate(@PathVariable int id, HttpServletRequest request){
        String ipAddress = getClientIpAddress(request);
        String user = getCurrentUser();
        String role = getCurrentUserRole();

        try {
            CertificateResponseDTO cert = certificateService.getCertificateById(id);
            certificateService.revokeCertificate(id);

            String issuerInfo = cert.getIssuerId() != null ? "Issuer ID: " + cert.getIssuerId() : "SELF_SIGNED";

            loggerService.logCertificateEvent(
                    "CERTIFICATE_REVOKED",
                    user,
                    role,
                    "SUCCESS",
                    "Certificate revoked successfully",
                    ipAddress,
                    String.valueOf(id),
                    cert.getCn(),
                    issuerInfo
            );
        } catch (Exception e) {
            loggerService.logCertificateEvent(
                    "CERTIFICATE_REVOKE_FAILED",
                    user,
                    role,
                    "FAILURE",
                    "Failed to revoke certificate: " + e.getMessage(),
                    ipAddress,
                    String.valueOf(id),
                    "UNKNOWN",
                    "UNKNOWN"
            );
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificateResponseDTO> getById(@PathVariable int id, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        String user = getCurrentUser();
        String role = getCurrentUserRole();

        try {
            CertificateResponseDTO cert = certificateService.getCertificateById(id);

            String issuerInfo = cert.getIssuer() != null ? cert.getIssuer() : "SELF_SIGNED";

            loggerService.logCertificateEvent(
                    "CERTIFICATE_ACCESSED",
                    user,
                    role,
                    "SUCCESS",
                    "Retrieved certificate details",
                    ipAddress,
                    String.valueOf(id),
                    cert.getCn(),
                    issuerInfo
            );

            return ResponseEntity.ok(cert);
        } catch (Exception e) {
            loggerService.logCertificateEvent(
                    "CERTIFICATE_ACCESS_FAILED",
                    user,
                    role,
                    "FAILURE",
                    "Failed to retrieve certificate: " + e.getMessage(),
                    ipAddress,
                    String.valueOf(id),
                    "UNKNOWN",
                    "UNKNOWN"
            );
            throw e;
        }
    }

    @GetMapping("/verify/{id}")
    public ResponseEntity<String> verifyCertificate(@PathVariable int id, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        String user = getCurrentUser();
        String role = getCurrentUserRole();

        try {
            CertificateResponseDTO cert = certificateService.getCertificateById(id);
            certificateService.verifyCertificateChain(id);

            String issuerInfo = cert.getIssuer() != null ? cert.getIssuer() : "SELF_SIGNED";

            loggerService.logCertificateEvent(
                    "CERTIFICATE_VERIFIED",
                    user,
                    role,
                    "SUCCESS",
                    "Certificate chain verified successfully",
                    ipAddress,
                    String.valueOf(id),
                    cert.getCn(),
                    issuerInfo
            );

            return ResponseEntity.ok("Certificate chain verification completed. Check server logs.");
        } catch (Exception e) {
            loggerService.logCertificateEvent(
                    "CERTIFICATE_VERIFICATION_FAILED",
                    user,
                    role,
                    "FAILURE",
                    "Certificate verification failed: " + e.getMessage(),
                    ipAddress,
                    String.valueOf(id),
                    "UNKNOWN",
                    "UNKNOWN"
            );

            return ResponseEntity.badRequest().body("Verification failed: " + e.getMessage());
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "ANONYMOUS";
    }

    private String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null && !auth.getAuthorities().isEmpty()) {
            return auth.getAuthorities().iterator().next().getAuthority();
        }
        return "UNKNOWN";
    }
}