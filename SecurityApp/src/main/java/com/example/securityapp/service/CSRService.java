package com.example.securityapp.service;

import com.example.securityapp.RepositoryInterfaces.CSRRepositoryInterface;
import com.example.securityapp.RepositoryInterfaces.CertificateRepositoryInterface;
import com.example.securityapp.domain.CSRStatus;
import com.example.securityapp.domain.Certificate;
import com.example.securityapp.domain.CertificateSigningRequest;
import com.example.securityapp.dto.CSRUploadRequestDTO;
import com.example.securityapp.dto.CSRUploadResponseDTO;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class CSRService {

    @Autowired
    private CSRRepositoryInterface csrRepository;

    @Autowired
    private CertificateRepositoryInterface certificateRepository;

    static {
        // register BouncyCastle once when class loads
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public CSRUploadResponseDTO uploadCSR(CSRUploadRequestDTO request) {

        // 1. Validacija fajla
        validateCSRFile(request.getCsrFile());

        // 2. Čitaj PEM sadržaj
        String pemContent = readFileContent(request.getCsrFile());

        // 3. Parse CSR i izvuci podatke
        CSRParseResult parseResult = parseCSRFromPEM(pemContent);

        // 4. Validuj odabrani CA
        Certificate selectedCA = certificateRepository.findById(Math.toIntExact(request.getSelectedCaId()))
                .orElseThrow(() -> new RuntimeException("Selected CA not found"));

        validateCACanIssue(selectedCA);

        // 5. Validuj trajanje
        validateRequestedDuration(request.getRequestedDurationDays(), selectedCA);

        // 6. Sačuvaj CSR entity
        CertificateSigningRequest csr = new CertificateSigningRequest();
        csr.setFilename(request.getCsrFile().getOriginalFilename());
        csr.setPemContent(pemContent);
        csr.setSubject(parseResult.getSubject());
        csr.setPublicKeyAlgorithm(parseResult.getPublicKeyAlgorithm());
        csr.setKeySize(parseResult.getKeySize());
        csr.setSelectedCaId(request.getSelectedCaId());
        csr.setRequestedDurationDays(request.getRequestedDurationDays());
        csr.setUploadedAt(LocalDateTime.now());
        //csr.setUploadedByUserId(getCurrentUserId());
        csr.setStatus(CSRStatus.PENDING);

        CertificateSigningRequest saved = csrRepository.save(csr);

        return new CSRUploadResponseDTO(
                saved.getId(),
                "CSR uploaded successfully. Awaiting admin approval."
        );
    }

    private CSRParseResult parseCSRFromPEM(String pemContent) {
        try {
            // Ukloni PEM headers
            String base64Content = pemContent
                    .replaceAll("-----BEGIN CERTIFICATE REQUEST-----", "")
                    .replaceAll("-----END CERTIFICATE REQUEST-----", "")
                    .replaceAll("\\s", "");

            // Decode iz Base64
            byte[] csrBytes = Base64.getDecoder().decode(base64Content);

            // Parse sa BouncyCastle
            PKCS10CertificationRequest pkcs10 = new PKCS10CertificationRequest(csrBytes);

            // Izvuci podatke
            String subject = pkcs10.getSubject().toString();
            SubjectPublicKeyInfo pubKeyInfo = pkcs10.getSubjectPublicKeyInfo();
            String algorithm = pubKeyInfo.getAlgorithm().getAlgorithm().getId();
            int keySize = calculateKeySize(pubKeyInfo);

            // Kreiraj plain objekt bez Lombok builder-a
            CSRParseResult result = new CSRParseResult();
            result.setSubject(subject);
            result.setPublicKeyAlgorithm(algorithm);
            result.setKeySize(keySize);
            result.setPkcs10(pkcs10);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSR: " + e.getMessage());
        }
    }

    private void validateCSRFile(MultipartFile csrFile) {
        if (csrFile == null || csrFile.isEmpty()) {
            throw new RuntimeException("CSR file is missing or empty.");
        }

        String filename = csrFile.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".pem") && !filename.endsWith(".csr"))) {
            throw new RuntimeException("CSR file must be a .pem or .csr file.");
        }
    }

    private String readFileContent(MultipartFile file) {
        try {
            return new String(file.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSR file content: " + e.getMessage(), e);
        }
    }

    private void validateCACanIssue(Certificate caCert) {
        if (caCert == null) {
            throw new RuntimeException("CA certificate not provided.");
        }
        if (!caCert.isCA()) {
            throw new RuntimeException("Selected certificate is not a valid CA.");
        }
        if (caCert.isRevoked()) {
            throw new RuntimeException("Selected CA is revoked and cannot issue certificates.");
        }
        if (caCert.getEndDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Selected CA has expired.");
        }
    }

    private void validateRequestedDuration(Integer requestedDays, Certificate caCert) {
        if (requestedDays == null || requestedDays <= 0) {
            throw new RuntimeException("Requested duration must be positive.");
        }
        long maxDays = java.time.Duration.between(
                LocalDateTime.now(),
                caCert.getEndDate().atStartOfDay()
        ).toDays();

        if (requestedDays > maxDays) {
            throw new RuntimeException("Requested duration exceeds CA certificate validity.");
        }
    }

    private int calculateKeySize(SubjectPublicKeyInfo pubKeyInfo) {
        try {
            // Pretvori SubjectPublicKeyInfo u standardni Java PublicKey
            PublicKey publicKey = new JcaPEMKeyConverter()
                    .setProvider("BC")
                    .getPublicKey(pubKeyInfo);

            if (publicKey instanceof RSAPublicKey) {
                // RSA -> keySize je dužina modula
                return ((RSAPublicKey) publicKey).getModulus().bitLength();
            } else if (publicKey instanceof ECPublicKey) {
                // EC -> keySize je dužina order-a krive
                return ((ECPublicKey) publicKey).getParams().getCurve().getField().getFieldSize();
            } else {
                throw new RuntimeException("Unsupported key algorithm: " + publicKey.getAlgorithm());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to determine key size: " + e.getMessage(), e);
        }
    }

    //private Long getCurrentUserId() {
        // Ako imaš Spring Security:
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         //UserPrincipal user = (UserPrincipal) auth.getPrincipal();
         //return user.getId();

        // Za sada samo dummy
        //return 1L;
    //}

}
