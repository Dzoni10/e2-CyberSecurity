package com.example.securityapp.dto;

import java.util.Map;

//KADA KORISNIK POPUNI FORMU ZA IZDAVANJE SERTIFIKATA BACK PRIMA OVO

public class CertificateRequestDTO {

        public String cn;        // Podaci o vlasniku (X500Name string ili posebna polja)
        public String o;
        public String ou;
        public String c;
        public Integer issuerId;         // ID CA sertifikata koji potpisuje
        public int durationInDays;    // trajanje sertifikata
        public boolean isRoot;
        public boolean isIntermediate;
        public boolean isEndEntity;
        public boolean isCA;
        public Map<String, String> extensions; // npr. keyUsage, basicConstraints, itd.

        public CertificateRequestDTO() {}

        public CertificateRequestDTO(String cn,String o, String ou,String c, Integer issuerId, int durationInDays,boolean isRoot, boolean isIntermediate,boolean isEndEntity,boolean isCA, Map<String, String> extensions) {
                this.cn = cn;
                this.o = o;
                this.ou = ou;
                this.c = c;
                this.issuerId = issuerId;
                this.durationInDays = durationInDays;
                this.isRoot = isRoot;
                this.isIntermediate = isIntermediate;
                this.isEndEntity = isEndEntity;
                this.isCA = isCA;
                this.extensions = extensions;
        }

}
