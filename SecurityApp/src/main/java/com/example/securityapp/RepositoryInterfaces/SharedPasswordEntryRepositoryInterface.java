package com.example.securityapp.RepositoryInterfaces;

import com.example.securityapp.domain.SharedPasswordEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedPasswordEntryRepositoryInterface extends JpaRepository<SharedPasswordEntry, Long> {
    List<SharedPasswordEntry> findBySharedWithUserId(Integer userId);
    List<SharedPasswordEntry> findByPasswordEntryId(Long passwordEntryId);
}