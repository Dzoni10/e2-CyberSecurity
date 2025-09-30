package com.example.securityapp.service;

import com.example.securityapp.domain.Certificate;
import com.example.securityapp.dto.CertificateRequestDTO;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.asn1.x9.X962NamedCurves;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class CertificateGenerator {

    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public static X509Certificate generateCertificate(
            String subjectDn,
            String issuerDn,
            String serialHex,
            LocalDate notBefore,
            LocalDate notAfter,
            KeyPair subjectKeyPair,
            PrivateKey issuerPrivateKey,
            boolean isRoot,
            boolean isIntermediate,
            boolean isEndEntity,
            boolean isCA,
            Map<String, String> extensions
    ) throws NoSuchAlgorithmException, OperatorCreationException, CertificateException, CertIOException {

        Map<String, String> subjectValues = parseDN(subjectDn);

        // ovde ide ceo BouncyCastle kod koji sam ti pisao gore
        X500Name subject = new X500Name("CN="+subjectValues.get("CN")+", O=" +subjectValues.get("O")+",OU="+subjectValues.get("OU")+", C=" + subjectValues.get("C"));

        Map<String, String> issuerValues = parseDN(issuerDn);

        X500Name issuer = new X500Name("CN=" + issuerValues.get("CN")+ ", O=" + issuerValues.get("O") + ", OU="+issuerValues.get("OU") +", C="+issuerValues.get("C"));

        Date start = Date.from(notBefore.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(notAfter.atStartOfDay(ZoneId.systemDefault()).toInstant());

        BigInteger serial = new BigInteger(serialHex, 16);

        // Builder
        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
                issuer,
                serial,
                start,
                end,
                subject,
                SubjectPublicKeyInfo.getInstance(subjectKeyPair.getPublic().getEncoded())
        );

        // Extension utils (SKI/AKI)
        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();

        // BasicConstraints (CA i pathLen)
        int pathLen = parseIntOrDefault(extensions, "pathLen", isCA ? 0 : -1);
        BasicConstraints bc = isCA ? new BasicConstraints(pathLen >= 0 ? pathLen : Integer.MAX_VALUE) : new BasicConstraints(false);
        certBuilder.addExtension(Extension.basicConstraints, true, bc);

        // KeyUsage
        int keyUsageBits = computeKeyUsageBits(extensions, isCA);
        certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(keyUsageBits));

        // ExtendedKeyUsage (opciono)
        ExtendedKeyUsage eku = buildExtendedKeyUsage(extensions);
        if (eku != null) {
            certBuilder.addExtension(Extension.extendedKeyUsage, false, eku);

        }
        // Subject Key Identifier
        SubjectKeyIdentifier ski = extUtils.createSubjectKeyIdentifier(subjectKeyPair.getPublic());
        certBuilder.addExtension(Extension.subjectKeyIdentifier, false, ski);

        AuthorityKeyIdentifier aki;

        // Authority Key Identifier = iz issuer public key-ja
        // Ako je self-signed (root), AKI == SKI
        if(subject.equals(issuer)){
            aki=extUtils.createAuthorityKeyIdentifier(subjectKeyPair.getPublic());
        }else {
            // U praksi: prosledio bi se issuer public key; ako ga nemaš pri ruci, koristi SKI subjekta kao fallback.
            aki = extUtils.createAuthorityKeyIdentifier(subjectKeyPair.getPublic());
        }
        certBuilder.addExtension(Extension.authorityKeyIdentifier, false, aki);

        // Potpis
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider("BC")
                .build(issuerPrivateKey);

        X509CertificateHolder holder = certBuilder.build(signer);

        return new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(holder);
    }


    private static int computeKeyUsageBits(Map<String, String> extensions, boolean isCA) {

        // Podržani tokeni u keyUsage (CSV): digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment,
        // keyAgreement, keyCertSign, cRLSign, encipherOnly, decipherOnly
        // Ako ništa nije dato: za end-entity -> digitalSignature|keyEncipherment; za CA -> keyCertSign|cRLSign
        String kuCsv = getOrNull(extensions, "keyUsage");
        Set<String> tokens = new HashSet<>();
        if (kuCsv != null && !kuCsv.isBlank()) {
            for (String t : kuCsv.split(",")) tokens.add(t.trim().toLowerCase(Locale.ROOT));
        } else {
            if (isCA) {
                tokens.add("keycertsign");
                tokens.add("crlsign");
            } else {
                tokens.add("digitalsignature");
                tokens.add("keyencipherment");
            }
        }

        int bits = 0;
        if (tokens.contains("digitalsignature")) bits |= KeyUsage.digitalSignature;
        if (tokens.contains("nonrepudiation") || tokens.contains("contentcommitment")) bits |= KeyUsage.nonRepudiation;
        if (tokens.contains("keyencipherment")) bits |= KeyUsage.keyEncipherment;
        if (tokens.contains("dataencipherment")) bits |= KeyUsage.dataEncipherment;
        if (tokens.contains("keyagreement")) bits |= KeyUsage.keyAgreement;
        if (tokens.contains("keycertsign")) bits |= KeyUsage.keyCertSign;
        if (tokens.contains("crlsign")) bits |= KeyUsage.cRLSign;
        if (tokens.contains("encipheronly")) bits |= KeyUsage.encipherOnly;
        if (tokens.contains("decipheronly")) bits |= KeyUsage.decipherOnly;

        return bits;
    }



    private static ExtendedKeyUsage buildExtendedKeyUsage(Map<String, String> extensions) {
        // extendedKeyUsage: CSV tokeni: serverAuth, clientAuth, codeSigning, emailProtection, timeStamping, OCSPSigning
        String ekuCsv = getOrNull(extensions, "extendedKeyUsage");
        if (ekuCsv == null || ekuCsv.isBlank()) return null;

        List<KeyPurposeId> ids = new ArrayList<>();
        for (String raw : ekuCsv.split(",")) {
            String t = raw.trim().toLowerCase(Locale.ROOT);
            switch (t) {
                case "serverauth": ids.add(KeyPurposeId.id_kp_serverAuth); break;
                case "clientauth": ids.add(KeyPurposeId.id_kp_clientAuth); break;
                case "codesigning": ids.add(KeyPurposeId.id_kp_codeSigning); break;
                case "emailprotection": ids.add(KeyPurposeId.id_kp_emailProtection); break;
                case "timestamping": ids.add(KeyPurposeId.id_kp_timeStamping); break;
                case "ocspsigning": ids.add(KeyPurposeId.id_kp_OCSPSigning); break;
                default:
                    // Ako želiš custom OID: npr. "1.3.6.1.5.5.7.3.1"
                    if (t.matches("\\d+(\\.\\d+)+")) {
                        ids.add(KeyPurposeId.getInstance(new ASN1ObjectIdentifier(t)));
                    }
            }
        }
        if (ids.isEmpty()) return null;
        return new ExtendedKeyUsage(ids.toArray(new KeyPurposeId[0]));
    }


    private static int parseIntOrDefault(Map<String, String> map, String key, int def) {
        try {
            String v = getOrNull(map, key);
            if (v == null) return def;
            return Integer.parseInt(v.trim());
        } catch (Exception e) {
            return def;
        }
    }

    private static String getOrNull(Map<String, String> map, String key) {
        if (map == null) return null;
        return map.getOrDefault(key, null);
    }

    public static Map<String, String> parseDN(String dn) {
        X500Name x500Name = new X500Name(dn);
        Map<String, String> values = new HashMap<>();

        RDN[] rdns = x500Name.getRDNs();
        for (RDN rdn : rdns) {
            if (rdn.getFirst() != null) {
                if (rdn.getFirst().getType().equals(BCStyle.CN)) {
                    values.put("CN", rdn.getFirst().getValue().toString());
                } else if (rdn.getFirst().getType().equals(BCStyle.O)) {
                    values.put("O", rdn.getFirst().getValue().toString());
                } else if (rdn.getFirst().getType().equals(BCStyle.OU)) {
                    values.put("OU", rdn.getFirst().getValue().toString());
                } else if (rdn.getFirst().getType().equals(BCStyle.C)) {
                    values.put("C", rdn.getFirst().getValue().toString());
                }
            }
        }
        return values;
    }

}
