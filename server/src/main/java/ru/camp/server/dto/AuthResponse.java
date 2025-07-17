package ru.camp.server.dto;

import lombok.Getter;
import lombok.Setter;
import ru.camp.server.model.Role;

@Setter
@Getter
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private Role role;
}