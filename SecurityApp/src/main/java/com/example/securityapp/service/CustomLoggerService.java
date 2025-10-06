package com.example.securityapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CustomLoggerService {

    private static final String BASE_LOG_DIR = "logging";
    private static final String DAYS_DIR = "days";
    private static final String AUTH_DIR = "register-login";
    private static final String PASSWORD_MANAGER_DIR = "passwordManager";
    private static final String CERTIFICATES_DIR = "certificates";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final ObjectMapper objectMapper;

    public CustomLoggerService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        initializeDirectories();
    }

    private void initializeDirectories() {
        try {
            Files.createDirectories(Paths.get(BASE_LOG_DIR, DAYS_DIR));
            Files.createDirectories(Paths.get(BASE_LOG_DIR, AUTH_DIR));
            Files.createDirectories(Paths.get(BASE_LOG_DIR, PASSWORD_MANAGER_DIR));
            Files.createDirectories(Paths.get(BASE_LOG_DIR, CERTIFICATES_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create log directories: " + e.getMessage());
        }
    }

    /**
     * Log authentication events (signup, login, verification)
     */
    public void logAuthEvent(String eventType, String email, String role, String result, String message, String ipAddress, String userAgent) {
        LogEntry logEntry = createLogEntry(eventType, "AUTHENTICATION", email, role, result, message, ipAddress, userAgent);

        // Write to both universal daily log and auth-specific log
        writeToUniversalLog(logEntry);
        writeToSpecificLog(logEntry, AUTH_DIR);
    }

    /**
     * Log password manager events
     */
    public void logPasswordManagerEvent(String eventType, String email, String role, String result, String message, String ipAddress) {
        LogEntry logEntry = createLogEntry(eventType, "PASSWORD_MANAGER", email, role, result, message, ipAddress, null);

        // Write to both universal daily log and password manager-specific log
        writeToUniversalLog(logEntry);
        writeToSpecificLog(logEntry, PASSWORD_MANAGER_DIR);
    }

    /**
     * Log general security events
     */
    public void logSecurityEvent(String eventType, String category, String user, String role, String result, String message, String ipAddress) {
        LogEntry logEntry = createLogEntry(eventType, category, user, role, result, message, ipAddress, null);
        writeToUniversalLog(logEntry);
    }

    /**
     * Log certificate events (issue, revoke, verify)
     */
    public void logCertificateEvent(String eventType, String user, String role, String result, String message, String ipAddress,
                                    String certificateId, String certificateCN, String issuerAlias) {
        CertificateLogEntry logEntry = createCertificateLogEntry(eventType, user, role, result, message, ipAddress,
                certificateId, certificateCN, issuerAlias);

        // Write to both universal daily log and certificate-specific log
        writeToUniversalLog(convertToLogEntry(logEntry));
        writeToSpecificCertificateLog(logEntry);
    }

    private LogEntry createLogEntry(String eventType, String category, String user, String role, String result, String message, String ipAddress, String userAgent) {
        LogEntry entry = new LogEntry();
        entry.timestamp = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.MILLIS).toString();
        entry.eventType = eventType;
        entry.category = category;
        entry.user = user;
        entry.role = role;
        entry.result = result;
        entry.message = message;
        entry.ipAddress = ipAddress;
        entry.userAgent = userAgent;
        entry.eventId = generateEventId();
        return entry;
    }

    private CertificateLogEntry createCertificateLogEntry(String eventType, String user, String role, String result, String message,
                                                          String ipAddress, String certificateId, String certificateCN, String issuerAlias) {
        CertificateLogEntry entry = new CertificateLogEntry();
        entry.timestamp = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.MILLIS).toString();
        entry.eventType = eventType;
        entry.category = "CERTIFICATE_MANAGEMENT";
        entry.user = user;
        entry.role = role;
        entry.result = result;
        entry.message = message;
        entry.ipAddress = ipAddress;
        entry.certificateId = certificateId;
        entry.certificateCN = certificateCN;
        entry.issuerAlias = issuerAlias;
        entry.eventId = generateEventId();
        return entry;
    }

    private LogEntry convertToLogEntry(CertificateLogEntry certEntry) {
        LogEntry entry = new LogEntry();
        entry.timestamp = certEntry.timestamp;
        entry.eventType = certEntry.eventType;
        entry.category = certEntry.category;
        entry.user = certEntry.user;
        entry.role = certEntry.role;
        entry.result = certEntry.result;
        entry.message = certEntry.message + " | CertID: " + certEntry.certificateId +
                " | CN: " + certEntry.certificateCN + " | Issuer: " + certEntry.issuerAlias;
        entry.ipAddress = certEntry.ipAddress;
        entry.userAgent = null;
        entry.eventId = certEntry.eventId;
        return entry;
    }

    private void writeToUniversalLog(LogEntry logEntry) {
        String date = LocalDateTime.now().format(DATE_FORMATTER);
        String fileName = date + ".log";
        Path filePath = Paths.get(BASE_LOG_DIR, DAYS_DIR, fileName);
        writeLogEntry(filePath, logEntry);
    }

    private void writeToSpecificLog(LogEntry logEntry, String directory) {
        String date = LocalDateTime.now().format(DATE_FORMATTER);
        String fileName = date + ".log";
        Path filePath = Paths.get(BASE_LOG_DIR, directory, fileName);
        writeLogEntry(filePath, logEntry);
    }

    private void writeToSpecificCertificateLog(CertificateLogEntry logEntry) {
        String date = LocalDateTime.now().format(DATE_FORMATTER);
        String fileName = date + ".log";
        Path filePath = Paths.get(BASE_LOG_DIR, CERTIFICATES_DIR, fileName);
        writeCertificateLogEntry(filePath, logEntry);
    }

    private void writeLogEntry(Path filePath, LogEntry logEntry) {
        try {
            File logFile = filePath.toFile();
            boolean isNewFile = !logFile.exists();

            try (FileWriter writer = new FileWriter(logFile, true)) {
                if (!isNewFile) {
                    writer.write("\n");
                }
                writer.write(objectMapper.writeValueAsString(logEntry));
                writer.write("\n");
            }

            checkAndRotateLog(filePath);

        } catch (IOException e) {
            System.err.println("Failed to write log entry: " + e.getMessage());
        }
    }

    private void writeCertificateLogEntry(Path filePath, CertificateLogEntry logEntry) {
        try {
            File logFile = filePath.toFile();
            boolean isNewFile = !logFile.exists();

            try (FileWriter writer = new FileWriter(logFile, true)) {
                if (!isNewFile) {
                    writer.write("\n");
                }
                writer.write(objectMapper.writeValueAsString(logEntry));
                writer.write("\n");
            }

            checkAndRotateLog(filePath);

        } catch (IOException e) {
            System.err.println("Failed to write certificate log entry: " + e.getMessage());
        }
    }

    private void checkAndRotateLog(Path filePath) throws IOException {
        long fileSizeInMB = Files.size(filePath) / (1024 * 1024);

        // Rotacija ako je fajl veći od 50MB
        if (fileSizeInMB > 50) {
            String originalName = filePath.getFileName().toString();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
            String rotatedName = originalName.replace(".log", "_part" + timestamp + ".log");
            Path rotatedPath = filePath.getParent().resolve(rotatedName);

            Files.move(filePath, rotatedPath);
            System.out.println("Log file rotated due to size: " + rotatedName);
        }
    }

    /**
     * Clean up old logs based on retention policy
     * Keeps logs for specified number of days
     */
    public void cleanupOldLogs(int retentionDays) {
        Instant cutoffDate = Instant.now().minus(retentionDays, java.time.temporal.ChronoUnit.DAYS);

        try {
            Files.walk(Paths.get(BASE_LOG_DIR))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".log"))
                    .filter(path -> {
                        try {
                            return Files.getLastModifiedTime(path)
                                    .toInstant()
                                    .isBefore(cutoffDate);
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            System.out.println("Deleted old log: " + path.getFileName());
                        } catch (IOException e) {
                            System.err.println("Failed to delete log: " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println("Failed to cleanup old logs: " + e.getMessage());
        }
    }

    private String generateEventId() {
        return String.format("%d-%d",
                System.currentTimeMillis(),
                (int)(Math.random() * 1000));
    }

    /**
     * Inner class representing a standard log entry
     */
    private static class LogEntry {
        public String timestamp;
        public String eventType;
        public String category;
        public String eventId;
        public String user;
        public String role;
        public String result;
        public String message;
        public String ipAddress;
        public String userAgent;
    }

    /**
     * Inner class representing a certificate log entry
     */
    private static class CertificateLogEntry {
        public String timestamp;
        public String eventType;
        public String category;
        public String eventId;
        public String user;
        public String role;
        public String result;
        public String message;
        public String ipAddress;
        public String certificateId;
        public String certificateCN;
        public String issuerAlias;
    }
}