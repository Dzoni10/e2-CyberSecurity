package com.example.securityapp.RepositoryInterfaces;

import com.example.securityapp.domain.KeyStoreMeta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyStoreMetaRepository extends JpaRepository<KeyStoreMeta, Integer> {
}
