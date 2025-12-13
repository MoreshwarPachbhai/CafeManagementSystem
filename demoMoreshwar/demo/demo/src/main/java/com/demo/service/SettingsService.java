package com.demo.service;

import com.demo.model.Settings;
import com.demo.repository.SettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SettingsService {

    private final SettingsRepository settingsRepo;

    public SettingsService(SettingsRepository settingsRepo) {
        this.settingsRepo = settingsRepo;
    }

    public Settings getSettings(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId must not be null");
        Settings s = settingsRepo.findByUserId(userId);
        if (s == null) {
            s = new Settings(userId, false, true);
            settingsRepo.save(s);
        }
        return s;
    }

    public Settings setDarkMode(Long userId, boolean enabled) {
        if (userId == null) throw new IllegalArgumentException("userId must not be null");
        Settings s = getSettings(userId);
        s.setDarkMode(enabled);
        return settingsRepo.save(s);
    }

    public Settings setSoundAlert(Long userId, boolean enabled) {
        if (userId == null) throw new IllegalArgumentException("userId must not be null");
        Settings s = getSettings(userId);
        s.setSoundAlert(enabled);
        return settingsRepo.save(s);
    }
}
