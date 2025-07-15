package ru.camp.server.dto;

public class UserLoginRequest {
    private String usernameOrEmail;
    private String password;
    // Геттеры и сеттеры
    public String getUsernameOrEmail() { return usernameOrEmail; }
    public String getPassword() { return password; }
} 