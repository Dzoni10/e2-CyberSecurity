package com.example.securityapp.controller;

import com.example.securityapp.RepositoryInterfaces.CertificateRepositoryInterface;
import com.example.securityapp.domain.Certificate;
import com.example.securityapp.domain.KeyStoreMeta;
import com.example.securityapp.domain.User;
import com.example.securityapp.dto.CertificateRequestDTO;
import com.example.securityapp.dto.CertificateResponseDTO;
import com.example.securityapp.dto.CertificateRevokeDTO;
import com.example.securityapp.service.CertificateService;
import com.example.securityapp.service.CustomLoggerService;
import com.example.securityapp.service.KeyStoreService;
import com.example.securityapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@Validated
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CertificateRepositoryInterface certificateRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomLoggerService loggerService;

    @Autowired
    private KeyStoreService keyStoreService;

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

    @GetMapping("/caOrg")
    public ResponseEntity<List<CertificateResponseDTO>> getAllCACertificatesByOrg(
            @RequestParam("userId") Integer userId,
            HttpServletRequest request) {

        String ipAddress = getClientIpAddress(request);
        String username = getCurrentUser();
        String role = getCurrentUserRole();

        // Fetch the user's organization using userId
        User user = userService.findById(userId);
        String organization = user.getOrganization();

        loggerService.logCertificateEvent(
                "CA_CERTIFICATE_LIST_ACCESSED",
                username,
                role,
                "SUCCESS",
                "Retrieved all CA certificates for organization: " + organization,
                ipAddress,
                "N/A",
                "N/A",
                "N/A"
        );

        // Fetch certificates by organization and isCA=true
        return ResponseEntity.ok(
                certificateService.getAllCACertificatesByOrg(organization)
        );
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
    public void revokeCertificate(@PathVariable int id,
                                  @RequestParam String reason, HttpServletRequest request){
        String ipAddress = getClientIpAddress(request);
        String user = getCurrentUser();
        String role = getCurrentUserRole();

        try {
            CertificateResponseDTO cert = certificateService.getCertificateById(id);
            certificateService.revokeCertificate(id, reason);

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

    @GetMapping("/id/{id}")
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

    @GetMapping("/by-user")
    public ResponseEntity<List<Certificate>> getUserCertificates(@RequestParam("userId") Integer userId) {
        List<Certificate> certs = certificateService.getEndEntityCertificatesForUser(userId);
        return ResponseEntity.ok(certs);
    }

    @GetMapping("/download/{certificateId}")
    public ResponseEntity<Resource> downloadCertificate(@PathVariable Integer certificateId) {
        //
        Certificate cert = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        KeyStoreMeta meta = keyStoreService.getMetaById(cert.getKeyStoreMetaId());

        Path filePath = Paths.get(meta.getPath());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("Keystore file not found: " + filePath);
        }

        Resource resource = new FileSystemResource(filePath);

        //response
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + cert.getAlias() + ".p12")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
/*
    @GetMapping("/download-public/{certificateId}")
    public ResponseEntity<byte[]> downloadPublicCertificate(@PathVariable Integer certificateId) {
        try {
            Certificate cert = certificateRepository.findById(certificateId)
                    .orElseThrow(() -> new RuntimeException("Certificate not found"));
            KeyStoreMeta meta = keyStoreService.getMetaById(cert.getKeyStoreMetaId());
            X509Certificate x509 = keyStoreService.loadCertificate(meta, cert.getAlias());

            byte[] encoded = x509.getEncoded();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + cert.getAlias() + ".cer")
                    .contentType(MediaType.valueOf("application/x-x509-ca-cert"))
                    .body(encoded);

        } catch (Exception e) {
            throw new RuntimeException("Failed to encode certificate: " + e.getMessage(), e);
        }
    }*/

    @GetMapping("/download-public/{certificateId}")
    public ResponseEntity<byte[]> downloadPublicCertificate(
            @PathVariable Integer certificateId,
            @RequestParam(defaultValue = "der") String format // "der" ili "pem"
    ) {
        try {
            // 1️⃣ Učitaj sertifikat iz baze i keystore-a
            Certificate cert = certificateRepository.findById(certificateId)
                    .orElseThrow(() -> new RuntimeException("Certificate not found"));

            KeyStoreMeta meta = keyStoreService.getMetaById(cert.getKeyStoreMetaId());
            X509Certificate x509 = keyStoreService.loadCertificate(meta, cert.getAlias());

            byte[] body;
            String filename;
            MediaType contentType;

            // 2️⃣ Odredi format
            if ("pem".equalsIgnoreCase(format)) {
                // PEM: Base64 + header/footer
                String pem = "-----BEGIN CERTIFICATE-----\n" +
                        Base64.getMimeEncoder(64, "\n".getBytes())
                                .encodeToString(x509.getEncoded()) +
                        "\n-----END CERTIFICATE-----\n";
                body = pem.getBytes(StandardCharsets.UTF_8);
                filename = cert.getAlias() + ".pem";
                contentType = MediaType.valueOf("application/x-pem-file");
            } else {
                // Default DER
                body = x509.getEncoded();
                filename = cert.getAlias() + ".cer";
                contentType = MediaType.valueOf("application/x-x509-ca-cert");
            }

            // 3️⃣ Vrati response
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(contentType)
                    .body(body);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate public certificate file: " + e.getMessage(), e);
        }
    }

/*
    @GetMapping("/keystore-password/{certificateId}")
    @PreAuthorize("hasRole('BASIC')") // samo vlasnik
    public ResponseEntity<String> getKeystorePassword(@PathVariable Integer certificateId) {
        Certificate cert = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        KeyStoreMeta meta = keyStoreService.getMetaById(cert.getKeyStoreMetaId());

        // Dekriptuj lozinku
        String decryptedPassword = keyStoreService.decryptPassword(meta.getEncryptedPassword());

        return ResponseEntity.ok(decryptedPassword);
    }*/

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