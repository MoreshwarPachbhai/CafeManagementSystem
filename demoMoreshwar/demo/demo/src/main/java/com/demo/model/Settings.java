package com.demo.model;

import jakarta.persistence.*;

@Entity
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private boolean darkMode;
    private boolean soundAlert;

    public Settings() {}

    public Settings(Long userId, boolean darkMode, boolean soundAlert) {
        this.userId = userId;
        this.darkMode = darkMode;
        this.soundAlert = soundAlert;
    }

    // ----------------------------
    // Getters and Setters
    // ----------------------------

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public boolean isSoundAlert() {
        return soundAlert;
    }

    public void setSoundAlert(boolean soundAlert) {
        this.soundAlert = soundAlert;
    }
}
