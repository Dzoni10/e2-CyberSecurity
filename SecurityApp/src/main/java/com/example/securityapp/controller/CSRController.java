package com.example.securityapp.controller;

import com.example.securityapp.dto.CSRDecisionDTO;
import com.example.securityapp.dto.CSRReviewDTO;
import com.example.securityapp.dto.CSRUploadRequestDTO;
import com.example.securityapp.dto.CSRUploadResponseDTO;
import com.example.securityapp.service.CSRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/csr")
public class CSRController {

    @Autowired
    private CSRService csrService;

    @PostMapping("/upload")
    public ResponseEntity<CSRUploadResponseDTO> uploadCSR(
            @RequestParam("csrFile") MultipartFile csrFile,
            @RequestParam("selectedCaId") Long selectedCaId,
            @RequestParam("requestedDurationDays") Integer duration) {

        //request DTO
        CSRUploadRequestDTO request = new CSRUploadRequestDTO();
        request.setCsrFile(csrFile);
        request.setSelectedCaId(selectedCaId);
        request.setRequestedDurationDays(duration);

        CSRUploadResponseDTO response = csrService.uploadCSR(request);
        return ResponseEntity.ok(response);
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
    public ResponseEntity<Void> processCSR(@RequestBody CSRDecisionDTO decision) {
        csrService.processCSRDecision(decision);
        return ResponseEntity.ok().build();
    }
}

 */
}
