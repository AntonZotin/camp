package ru.camp.server.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserLoginRequest {
    private String usernameOrEmail;
    private String password;
} 