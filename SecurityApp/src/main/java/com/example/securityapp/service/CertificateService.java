package com.example.securityapp.service;

import com.example.securityapp.RepositoryInterfaces.CertificateRepositoryInterface;
import com.example.securityapp.RepositoryInterfaces.UserRepositoryInterface;
import com.example.securityapp.config.CustomUserDetails;
import com.example.securityapp.domain.Certificate;
import com.example.securityapp.domain.KeyStoreMeta;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CertificateService {

    private final CertificateRepositoryInterface certificateRepository;
    private final UserRepositoryInterface userRepositoryInterface;
    private final KeyStoreService keyStoreService;

    @Autowired
    public CertificateService(CertificateRepositoryInterface certificateRepository, UserRepositoryInterface userRepositoryInterface, KeyStoreService keyStoreService) {
        this.certificateRepository = certificateRepository;
        this.userRepositoryInterface = userRepositoryInterface;
        this.keyStoreService = keyStoreService;
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

            String alias = "cert-" + serialHex;
            // 4) Datumi
            LocalDate start = LocalDate.now();
            LocalDate end = start.plusDays(request.durationInDays);

            // 5) Subject/Issuer DN (pretpostavka: request.subject i issuer.getSubject() su X500 DN stringovi, npr "CN=John, O=Org, C=RS")
            String subjectDn = "CN=" + request.cn +
                    ", O=" + request.o +
                    (request.ou != null && !request.ou.isBlank() ? ", OU=" + request.ou : "") +
                    ", C=" + request.c;

            // 6) Issuer PrivateKey (decode iz Base64 PKCS#8)
            PrivateKey issuerPrivateKey = subjectKeyPair.getPrivate();
            String issuerDn =subjectDn;


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
                        null,
                        request.isRoot,
                        request.isIntermediate,
                        request.isEndEntity,
                        request.isCA,
                        request.extensions
                );
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate certificate: " + e.getMessage(), e);
            }

            Map<String,String> values = CertificateGenerator.parseDN(subjectDn);

            // 8) Upis u entitet
            Certificate certificate = new Certificate();
            certificate.setAlias(alias);
            certificate.setSerialNumber(serialHex);
            certificate.setCn(values.get("CN"));
            certificate.setO(values.get("O"));
            certificate.setOu(values.get("OU"));
            certificate.setC(values.get("C"));
            certificate.setIssuer(issuerDn);
            certificate.setStartDate(start);
            certificate.setEndDate(end);
            certificate.setRoot(request.isRoot);
            certificate.setIntermediate(request.isIntermediate);
            certificate.setEndEntity(request.isEndEntity);
            certificate.setCA(request.isCA);
            certificate.setRevoked(false);
            certificate.setExtensions(String.valueOf(request.extensions)); // možeš JSON stringify ako želiš

            // 1. Cuvanje u keystore
            KeyStoreMeta meta = keyStoreService.createAndStoreKeyStore(
                    new X509Certificate[]{x509},
                    subjectKeyPair.getPrivate(),
                    alias,
                    userId
            );

// 2. Certificate entitet čuva samo referencu
            certificate.setKeyStoreMetaId(meta.getId());

            Certificate saved = certificateRepository.save(certificate);

            return new CertificateResponseDTO(
                    saved.getId(),
                    saved.getAlias(),
                    saved.getSerialNumber(),
                    saved.getCn(),
                    saved.getO(),
                    saved.getOu(),
                    saved.getC(),
                    saved.getIssuer(),
                    saved.getStartDate(),
                    saved.getEndDate(),
                    saved.getIssuerId(),
                    saved.isRoot(),
                    saved.isIntermediate(),
                    saved.isEndEntity(),
                    saved.isCA(),
                    saved.isRevoked()
            );
        }


        /// KREIRANJE SERTIFIKATA AKO ISSUER POSTOJI STAVLJANJE U LANAC ISPOD ROOT SERTIFIKATA
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

        if (request.isIntermediate) {
            if (!issuer.isRoot()) {
                throw new RuntimeException("Intermediate certificate must be issued only by root cert");
            }

            if (!request.isCA) {
                throw new RuntimeException("Intermediate certificate must have isCA=true to issue other certificates.");
            }

            if (request.isRoot) {
                throw new RuntimeException("Certificate cannot be both root and intermediate.");
            }
        }

        if (request.isEndEntity) {
            if (!issuer.isIntermediate()) {
                throw new RuntimeException("End-entity certificate can only be issued by an intermediate certificate. " +
                        "Issuer (ID: " + issuer.getId() + ") is not an intermediate certificate.");
            }
            if (request.isCA) {
                throw new RuntimeException("End-entity certificate cannot be a CA.");
            }
            if (request.isRoot || request.isIntermediate) {
                throw new RuntimeException("End-entity certificate cannot be root or intermediate.");
            }
        }

        LocalDate requestedEnd = LocalDate.now().plusDays(request.durationInDays);
        if (requestedEnd.isAfter(issuer.getEndDate())) {
            throw new RuntimeException("Certificate validity period cannot exceed issuer's validity. " +
                    "Issuer expires on: " + issuer.getEndDate() +
                    ", requested end date: " + requestedEnd);
        }

        // 2) Generiši subject key pair (RSA 2048)
        KeyPair subjectKeyPair = generateRsaKeyPair();

        // 3) Serijski broj (hex) – koristimo isti i u X.509 i u bazi
        String serialHex = Long.toHexString(System.nanoTime());

        String alias = "cert-" + serialHex;

        // 4) Datumi
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(request.durationInDays);

        // 5) Subject/Issuer DN (pretpostavka: request.subject i issuer.getSubject() su X500 DN stringovi, npr "CN=John, O=Org, C=RS")
        String subjectDn = "CN=" + request.cn+
                ", O=" + request.o +
                (request.ou != null ? ", OU=" + request.ou : "") +
                ", C=" + request.c;
        String issuerDn = "CN=" + issuer.getCn() +
                ", O=" + issuer.getO() +
                (issuer.getOu() != null ? ", OU=" + issuer.getOu() : "") +
                ", C=" + issuer.getC();


        // 6) Issuer PrivateKey (decode iz Base64 PKCS#8)
        KeyStoreMeta issuerMeta = keyStoreService.getMetaById(issuer.getKeyStoreMetaId());
        PrivateKey issuerPrivateKey = keyStoreService.loadPrivateKey(
                issuerMeta,
                issuer.getAlias()
        );

        if (issuerPrivateKey == null) {
            throw new RuntimeException("Issuer private key is null for alias: " + issuer.getAlias());
        }

        X509Certificate issuerCert = keyStoreService.loadCertificate(issuerMeta, issuer.getAlias());

        if (issuerCert == null) {
            throw new RuntimeException("Issuer certificate is null for alias: " + issuer.getAlias());
        }

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
                    issuerCert,
                    request.isRoot,
                    request.isIntermediate,
                    request.isEndEntity,
                    request.isCA,
                    request.extensions
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate certificate: " + e.getMessage(), e);
        }

//        // 8) Upis u entitet
//        Certificate certificate = new Certificate();
//        certificate.setAlias(alias);
//        certificate.setSerialNumber(serialHex);
//        certificate.setCn(request.cn);
//        certificate.setO(request.o);
//        certificate.setOu(request.ou);
//        certificate.setC(request.c);
//        certificate.setIssuer(issuerDn);
//        certificate.setStartDate(start);
//        certificate.setEndDate(end);
//        certificate.setRoot(request.isRoot);
//        certificate.setIntermediate(request.isIntermediate);
//        certificate.setEndEntity(request.isEndEntity);
//        certificate.setCA(request.isCA);
//        certificate.setRevoked(false);
//        certificate.setExtensions(String.valueOf(request.extensions)); // možeš JSON stringify ako želiš

//        // 1. Cuvanje u keystore (subject cert i private key)
//        KeyStoreMeta meta = keyStoreService.createAndStoreKeyStore(
//                new X509Certificate[]{x509},
//                subjectKeyPair.getPrivate(),
//                alias,
//                userId
//        );

// KORISTI (novi kod sa chain-om):
        Certificate certificate = new Certificate();
        certificate.setAlias(alias);
        certificate.setSerialNumber(serialHex);
        certificate.setCn(request.cn);
        certificate.setO(request.o);
        certificate.setOu(request.ou);
        certificate.setC(request.c);
        certificate.setIssuer(issuerDn);
        certificate.setStartDate(start);
        certificate.setEndDate(end);
        certificate.setRoot(request.isRoot);
        certificate.setIntermediate(request.isIntermediate);
        certificate.setEndEntity(request.isEndEntity);
        certificate.setCA(request.isCA);
        certificate.setRevoked(false);
        certificate.setExtensions(String.valueOf(request.extensions));
        certificate.setIssuerId(issuer.getId()); // DODATO: čuvaj issuer ID!

        // Gradi kompletan chain
        List<X509Certificate> chainList = buildCertificateChain(certificate, x509);

        // Sačuvaj sa chain-om
        KeyStoreMeta meta = keyStoreService.createAndStoreKeyStore(
                chainList.toArray(new X509Certificate[0]),
                subjectKeyPair.getPrivate(),
                alias,
                userId
        );

// 2. Certificate entitet čuva samo referencu
        certificate.setKeyStoreMetaId(meta.getId());

        Certificate saved = certificateRepository.save(certificate);

        return new CertificateResponseDTO(
                saved.getId(),
                saved.getAlias(),
                saved.getSerialNumber(),
                saved.getCn(),
                saved.getO(),
                saved.getOu(),
                saved.getC(),
                saved.getIssuer(),
                saved.getStartDate(),
                saved.getEndDate(),
                saved.getIssuerId(),
                saved.isRoot(),
                saved.isIntermediate(),
                saved.isEndEntity(),
                saved.isCA(),
                saved.isRevoked()
        );

    }

    public List<CertificateResponseDTO> getAllCertificates() {
        return certificateRepository.findAll().stream().map(c->new CertificateResponseDTO(
                c.getId(),
                c.getAlias(),
                c.getSerialNumber(),
                c.getCn(),
                c.getO(),
                c.getOu(),
                c.getC(),
                c.getIssuer(),
                c.getStartDate(),
                c.getEndDate(),
                c.getIssuerId(),
                c.isRoot(),
                c.isIntermediate(),
                c.isEndEntity(),
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
                c.getCn(),
                c.getO(),
                c.getOu(),
                c.getC(),
                c.getIssuer(),
                c.getStartDate(),
                c.getEndDate(),
                c.getIssuerId(),
                c.isRoot(),
                c.isIntermediate(),
                c.isEndEntity(),
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

    public CertificateResponseDTO getCertificateById(int id) {
        Certificate c = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        return new CertificateResponseDTO(
                c.getId(),
                c.getAlias(),
                c.getSerialNumber(),
                c.getCn(),
                c.getO(),
                c.getOu(),
                c.getC(),
                c.getIssuer(),
                c.getStartDate(),
                c.getEndDate(),
                c.getIssuerId(),
                c.isRoot(),
                c.isIntermediate(),
                c.isEndEntity(),
                c.isCA(),
                c.isRevoked()
        );
    }

    private List<X509Certificate> buildCertificateChain(Certificate subject, X509Certificate subjectX509) {
        List<X509Certificate> chain = new ArrayList<>();
        chain.add(subjectX509); // Subject cert ide prvi

        Certificate currentIssuer = null;
        if (subject.getIssuerId() != null) {
            currentIssuer = certificateRepository.findById(subject.getIssuerId()).orElse(null);
        }

        // Kreni od issuer-a i idi do root-a
        while (currentIssuer != null) {
            try {
                KeyStoreMeta issuerMeta = keyStoreService.getMetaById(currentIssuer.getKeyStoreMetaId());
                X509Certificate issuerX509 = keyStoreService.loadCertificate(issuerMeta, currentIssuer.getAlias());
                chain.add(issuerX509);

                // Ako je root, zaustavi se
                if (currentIssuer.isRoot()) {
                    break;
                }

                // Nastavi sa sledećim issuer-om
                if (currentIssuer.getIssuerId() != null) {
                    currentIssuer = certificateRepository.findById(currentIssuer.getIssuerId()).orElse(null);
                } else {
                    break;
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to build certificate chain: " + e.getMessage(), e);
            }
        }

        return chain;
    }

    public void verifyCertificateChain(int certificateId) {
        Certificate cert = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        KeyStoreMeta meta = keyStoreService.getMetaById(cert.getKeyStoreMetaId());
        KeyStore ks = keyStoreService.loadKeyStore(meta);

        try {
            // Učitaj chain iz keystore-a
            java.security.cert.Certificate[] chain = ks.getCertificateChain(cert.getAlias());

            if (chain == null || chain.length == 0) {
                System.out.println("No certificate chain found for: " + cert.getAlias());
                return;
            }

            System.out.println("Certificate chain for: " + cert.getCn());
            System.out.println("Chain length: " + chain.length);

            for (int i = 0; i < chain.length; i++) {
                X509Certificate x509 = (X509Certificate) chain[i];
                System.out.println((i+1) + ". Subject: " + x509.getSubjectX500Principal());
                System.out.println("   Issuer:  " + x509.getIssuerX500Principal());

                // Verifikuj potpis (osim za root koji je self-signed)
                if (i < chain.length - 1) {
                    X509Certificate issuerCert = (X509Certificate) chain[i + 1];
                    try {
                        x509.verify(issuerCert.getPublicKey());
                        System.out.println("   ✓ Signature verified by next certificate in chain");
                    } catch (Exception e) {
                        System.out.println("   ✗ Signature verification FAILED: " + e.getMessage());
                    }
                } else {
                    // Root cert - verifikuj self-signed
                    try {
                        x509.verify(x509.getPublicKey());
                        System.out.println("   ✓ Self-signed signature verified (root)");
                    } catch (Exception e) {
                        System.out.println("   ✗ Self-signed verification FAILED: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Chain verification failed: " + e.getMessage(), e);
        }
    }

}
