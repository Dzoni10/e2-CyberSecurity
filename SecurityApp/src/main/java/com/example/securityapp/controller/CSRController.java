package com.example.securityapp.controller;

import com.example.securityapp.dto.CSRDecisionDTO;
import com.example.securityapp.dto.CSRReviewDTO;
import com.example.securityapp.dto.CSRUploadRequestDTO;
import com.example.securityapp.dto.CSRUploadResponseDTO;
import com.example.securityapp.service.CSRService;
import com.example.securityapp.validation.ValidFileExtension;
import com.example.securityapp.validation.ValidFileSize;
import com.example.securityapp.validation.ValidationConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/csr")
@Validated
public class CSRController {

    @Autowired
    private CSRService csrService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadCSR(
            @RequestParam("csrFile")
            @NotNull(message = "CSR file is required")
            MultipartFile csrFile,

            @RequestParam("selectedCaId")
            @NotNull(message = "CA selection is required")
            @Positive(message = "Selected CA ID must be positive")
            Long selectedCaId,

            @RequestParam("requestedDurationDays")
            @NotNull(message = "Duration is required")
            @Min(value = ValidationConstants.MIN_DURATION_DAYS,
                    message = "Duration must be at least {value} day")
            @Max(value = ValidationConstants.MAX_DURATION_DAYS,
                    message = "Duration cannot exceed {value} days")
            Integer duration) {

        // Manual validation for file extension
        if (csrFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("CSR file is required");
        }

        String filename = csrFile.getOriginalFilename();
        if (filename == null || !isValidFileExtension(filename, ".csr", ".pem")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("CSR file must have .csr or .pem extension");
        }

        // Manual validation for file size
        if (csrFile.getSize() > ValidationConstants.MAX_FILE_SIZE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ValidationConstants.FILE_SIZE_EXCEEDED_MSG);
        }

        // Create request DTO
        CSRUploadRequestDTO request = new CSRUploadRequestDTO();
        request.setCsrFile(csrFile);
        request.setSelectedCaId(selectedCaId);
        request.setRequestedDurationDays(duration);

        CSRUploadResponseDTO response = csrService.uploadCSR(request);
        return ResponseEntity.ok(response);
    }

    private boolean isValidFileExtension(String filename, String... allowedExtensions) {
        String lowerFilename = filename.toLowerCase();
        return Arrays.stream(allowedExtensions)
                .anyMatch(lowerFilename::endsWith);
    }

    /*
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CSRReviewDTO>> getPendingCSRs() {
        List<CSRReviewDTO> pending = csrService.getPendingCSRs();
        return ResponseEntity.ok(pending);
    }

    @PostMapping("/process")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> processCSR(@Valid @RequestBody CSRDecisionDTO decision) {
        csrService.processCSRDecision(decision);
        return ResponseEntity.ok().build();
    }
    */
}