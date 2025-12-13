package com.demo.repository;

import com.demo.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Long> {
    // The service uses this specific method name, so we must define it here
    Settings findByUserId(Long userId);
}