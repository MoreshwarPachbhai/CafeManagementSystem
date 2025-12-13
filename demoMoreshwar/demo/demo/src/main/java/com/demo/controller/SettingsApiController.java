package com.demo.controller;

import com.demo.model.Settings;
import com.demo.service.SettingsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingsApiController {

    private final SettingsService settingsService;

    public SettingsApiController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping("/{userId}")
    public Settings getUserSettings(@PathVariable Long userId) {
        return settingsService.getSettings(userId);
    }

    @PostMapping("/{userId}/dark-mode")
    public Settings updateDarkMode(
            @PathVariable Long userId,
            @RequestParam boolean enabled) {
        // FIXED: Changed 'updateDarkMode' to 'setDarkMode' to match Service
        return settingsService.setDarkMode(userId, enabled);
    }

    @PostMapping("/{userId}/sound-alert")
    public Settings updateSoundAlert(
            @PathVariable Long userId,
            @RequestParam boolean enabled) {
        // FIXED: Changed 'updateSoundAlert' to 'setSoundAlert' to match Service
        return settingsService.setSoundAlert(userId, enabled);
    }
}