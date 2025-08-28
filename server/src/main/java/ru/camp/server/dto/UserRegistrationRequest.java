package ru.camp.server.dto;

import lombok.Getter;
import lombok.Setter;
import ru.camp.server.model.Role;

import java.util.Set;

@Setter
@Getter
public class UserRegistrationRequest {
    private String username;
    private String email;
    private String password;
    private Role role;
    private EmployeeDto employee;
    private Set<ChildDto> children;
} 