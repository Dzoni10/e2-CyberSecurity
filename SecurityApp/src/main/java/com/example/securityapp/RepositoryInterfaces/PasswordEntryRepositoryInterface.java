package com.example.securityapp.RepositoryInterfaces;

import com.example.securityapp.domain.PasswordEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordEntryRepositoryInterface extends JpaRepository<PasswordEntry, Long> {
    List<PasswordEntry> findByOwnerId(Integer ownerId);
}