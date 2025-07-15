package ru.camp.server.dto;

import ru.camp.server.model.Role;
import ru.camp.server.model.UserType;
import java.util.Set;

public class UserRegistrationRequest {
    private String username;
    private String email;
    private String password;
    private UserType userType;
    private Role role;
    private String fullName; // для сотрудника
    private String position; // для сотрудника
    private Set<ChildDto> children; // для родителя
    // Геттеры и сеттеры
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public UserType getUserType() { return userType; }
    public Role getRole() { return role; }
    public String getFullName() { return fullName; }
    public String getPosition() { return position; }
    public Set<ChildDto> getChildren() { return children; }
} 