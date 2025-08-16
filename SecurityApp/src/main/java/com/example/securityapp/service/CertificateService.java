package com.example.securityapp.service;

import com.example.securityapp.RepositoryInterfaces.CertificateRepositoryInterface;
import com.example.securityapp.domain.Certificate;
import com.example.securityapp.dto.CertificateRequestDTO;
import com.example.securityapp.dto.CertificateResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CertificateService {

    private final CertificateRepositoryInterface certificateRepository;

    @Autowired
    public CertificateService(CertificateRepositoryInterface certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    public CertificateResponseDTO issueCertificate(CertificateRequestDTO request){


        //FALI LOGIKA ZA KREIRANJE KLJUCEVA

        Certificate certificate = new Certificate();

        certificate.setAlias("cert-"+System.currentTimeMillis());
        certificate.setSerialNumber(Long.toHexString(System.nanoTime()));
        certificate.setSubject(request.subject);

        Certificate issuer = certificateRepository.findById(request.issuerId).orElseThrow(()->new RuntimeException("Issuer not found"));

        certificate.setIssuer(issuer.getSubject());

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(request.durationInDays);

        certificate.setStartDate(start);
        certificate.setEndDate(end);
        certificate.setCA(request.isCA);
        certificate.setRevoked(false);
        certificate.setExtensions(request.extensions.toString());

        //dok se ne smilsi algoritam za privatni i javni kljuc

        certificate.setPublicKey("dummy-public-key");
        certificate.setPrivateKey("dummy-private-key");

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

    public void revokeCertificate(int id){
        Certificate certificate = certificateRepository.findById(id).orElseThrow(()-> new RuntimeException("Certificate not found") );
        certificate.setRevoked(true);
        certificateRepository.save(certificate);
    }
}
