package com.example.securityapp.service;

import com.example.securityapp.RepositoryInterfaces.CertificateRepositoryInterface;
import com.example.securityapp.RepositoryInterfaces.UserRepositoryInterface;
import com.example.securityapp.config.CustomUserDetails;
import com.example.securityapp.domain.Certificate;
import com.example.securityapp.domain.User;
import com.example.securityapp.dto.CertificateRequestDTO;
import com.example.securityapp.dto.CertificateResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CertificateService {

    private final CertificateRepositoryInterface certificateRepository;
    private final UserRepositoryInterface userRepositoryInterface;

    @Autowired
    public CertificateService(CertificateRepositoryInterface certificateRepository, UserRepositoryInterface userRepositoryInterface) {
        this.certificateRepository = certificateRepository;
        this.userRepositoryInterface = userRepositoryInterface;
    }

    public CertificateResponseDTO issueCertificate(CertificateRequestDTO request){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Object principal = auth.getPrincipal();
        Integer userId = null;

        if (principal instanceof CustomUserDetails) {
            userId = ((CustomUserDetails)principal).getUser().getId();
        }else if(principal instanceof Integer)
        {
            userId =(Integer) principal;
        }else {
            throw new RuntimeException("Uexpected principal type: " +principal);
        }

        String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_","");

        if(request.issuerId==null) {
            if (!"ADMIN".equals(role)) {
                throw new RuntimeException("Only admin can create root certificate");
            }

            // 2) Generiši subject key pair (RSA 2048)
            KeyPair subjectKeyPair = generateRsaKeyPair();

            // 3) Serijski broj (hex) – koristimo isti i u X.509 i u bazi
            String serialHex = Long.toHexString(System.nanoTime());

            // 4) Datumi
            LocalDate start = LocalDate.now();
            LocalDate end = start.plusDays(request.durationInDays);

            // 5) Subject/Issuer DN (pretpostavka: request.subject i issuer.getSubject() su X500 DN stringovi, npr "CN=John, O=Org, C=RS")
            String subjectDn = request.subject;

            // 6) Issuer PrivateKey (decode iz Base64 PKCS#8)
            PrivateKey issuerPrivateKey = subjectKeyPair.getPrivate();
            String issuerDn = subjectDn;


            // 7) Generiši X.509
            X509Certificate x509;
            try {
                x509 = CertificateGenerator.generateCertificate(
                        subjectDn,
                        issuerDn,
                        serialHex,
                        start,
                        end,
                        subjectKeyPair,
                        issuerPrivateKey,
                        request.isCA,
                        request.extensions
                );
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate certificate: " + e.getMessage(), e);
            }

            // 8) Upis u entitet
            Certificate certificate = new Certificate();
            certificate.setAlias("cert-" + System.currentTimeMillis());
            certificate.setSerialNumber(serialHex);
            certificate.setSubject(subjectDn);
            certificate.setIssuer(issuerDn);
            certificate.setStartDate(start);
            certificate.setEndDate(end);
            certificate.setCA(request.isCA);
            certificate.setRevoked(false);
            certificate.setExtensions(String.valueOf(request.extensions)); // možeš JSON stringify ako želiš

            // Sačuvaj ključeve (Base64 DER)
            String pubKeyB64 = Base64.getEncoder().encodeToString(subjectKeyPair.getPublic().getEncoded());
            String privKeyB64 = Base64.getEncoder().encodeToString(subjectKeyPair.getPrivate().getEncoded());
            certificate.setPublicKey(pubKeyB64);
            certificate.setPrivateKey(privKeyB64);

            // Sačuvaj ceo X.509 (DER u Base64)
            try {
                certificate.setEncodedCertificate(Base64.getEncoder().encodeToString(x509.getEncoded()));
            } catch (Exception ex) {
                throw new RuntimeException("Failed to encode X.509: " + ex.getMessage(), ex);
            }

            Certificate saved = certificateRepository.save(certificate);

            return new CertificateResponseDTO(
                    saved.getId(),
                    saved.getAlias(),
                    saved.getSerialNumber(),
                    saved.getSubject(),
                    saved.getIssuer(),
                    saved.getStartDate(),
                    saved.getEndDate(),
                    saved.isCA(),
                    saved.isRevoked()
            );
        }


        Certificate issuer = certificateRepository.findById(request.issuerId).orElseThrow(() -> new RuntimeException("Issuer not found"));

        if (issuer.isRevoked()) {
            throw new RuntimeException("Issuer certificate is revoked.");
        }
        if (issuer.getEndDate() != null && issuer.getEndDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Issuer certificate is expired.");
        }
        if (!issuer.isCA()) {
            throw new RuntimeException("Issuer is not a CA; cannot issue new certificates.");
        }

        // 2) Generiši subject key pair (RSA 2048)
        KeyPair subjectKeyPair = generateRsaKeyPair();

        // 3) Serijski broj (hex) – koristimo isti i u X.509 i u bazi
        String serialHex = Long.toHexString(System.nanoTime());

        // 4) Datumi
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(request.durationInDays);

        // 5) Subject/Issuer DN (pretpostavka: request.subject i issuer.getSubject() su X500 DN stringovi, npr "CN=John, O=Org, C=RS")
        String subjectDn = request.subject;
        String issuerDn = issuer.getSubject();

        // 6) Issuer PrivateKey (decode iz Base64 PKCS#8)
        PrivateKey issuerPrivateKey = decodePrivateKeyFromBase64(issuer.getPrivateKey());

        X509Certificate x509;
        try {
            x509 = CertificateGenerator.generateCertificate(
                    subjectDn,
                    issuerDn,
                    serialHex,
                    start,
                    end,
                    subjectKeyPair,
                    issuerPrivateKey,
                    request.isCA,
                    request.extensions
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate certificate: " + e.getMessage(), e);
        }

        // 8) Upis u entitet
        Certificate certificate = new Certificate();
        certificate.setAlias("cert-" + System.currentTimeMillis());
        certificate.setSerialNumber(serialHex);
        certificate.setSubject(subjectDn);
        certificate.setIssuer(issuerDn);
        certificate.setStartDate(start);
        certificate.setEndDate(end);
        certificate.setCA(request.isCA);
        certificate.setRevoked(false);
        certificate.setExtensions(String.valueOf(request.extensions)); // možeš JSON stringify ako želiš

        // Sačuvaj ključeve (Base64 DER)
        String pubKeyB64 = Base64.getEncoder().encodeToString(subjectKeyPair.getPublic().getEncoded());
        String privKeyB64 = Base64.getEncoder().encodeToString(subjectKeyPair.getPrivate().getEncoded());
        certificate.setPublicKey(pubKeyB64);
        certificate.setPrivateKey(privKeyB64);

        // Sačuvaj ceo X.509 (DER u Base64)
        try {
            certificate.setEncodedCertificate(Base64.getEncoder().encodeToString(x509.getEncoded()));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to encode X.509: " + ex.getMessage(), ex);
        }

        Certificate saved = certificateRepository.save(certificate);

        return new CertificateResponseDTO(
                saved.getId(),
                saved.getAlias(),
                saved.getSerialNumber(),
                saved.getSubject(),
                saved.getIssuer(),
                saved.getStartDate(),
                saved.getEndDate(),
                saved.isCA(),
                saved.isRevoked()
        );

    }

    public List<CertificateResponseDTO> getAllCertificates() {
        return certificateRepository.findAll().stream().map(c->new CertificateResponseDTO(
                c.getId(),
                c.getAlias(),
                c.getSerialNumber(),
                c.getSubject(),
                c.getIssuer(),
                c.getStartDate(),
                c.getEndDate(),
                c.isCA(),
                c.isRevoked()
        )).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CertificateResponseDTO> getAllCACertificates() {
        return certificateRepository.findByIsCATrue().stream().map(c->new CertificateResponseDTO(
                c.getId(),
                c.getAlias(),
                c.getSerialNumber(),
                c.getSubject(),
                c.getIssuer(),
                c.getStartDate(),
                c.getEndDate(),
                c.isCA(),
                c.isRevoked()
        )).collect(Collectors.toList());
    }

    public void revokeCertificate(int id){
        Certificate certificate = certificateRepository.findById(id).orElseThrow(()-> new RuntimeException("Certificate not found") );
        certificate.setRevoked(true);
        certificateRepository.save(certificate);
    }


    private static KeyPair generateRsaKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("RSA not supported", e);
        }
    }

    private static PrivateKey decodePrivateKeyFromBase64(String base64Pkcs8) {
        try {
            byte[] pkcs8 = Base64.getDecoder().decode(base64Pkcs8);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pkcs8);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode issuer private key", e);
        }
    }

}
