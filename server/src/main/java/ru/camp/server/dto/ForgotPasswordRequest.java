package ru.camp.server.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ForgotPasswordRequest {
    private String email;
} 