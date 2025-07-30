package ru.camp.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSettingsRequest {
    private boolean emailNotifications;
    private boolean smsNotifications;
    private String theme;
} 