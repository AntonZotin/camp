package ru.camp.server.dto;

import ru.camp.server.model.Role;

public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private Role role;
    // Геттеры и сеттеры
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setRole(Role role) { this.role = role; }
} 