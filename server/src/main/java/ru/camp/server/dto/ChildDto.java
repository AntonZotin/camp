package ru.camp.server.dto;

import java.time.LocalDate;

public class ChildDto {
    private String name;
    private LocalDate birthDate;
    // Геттеры и сеттеры
    public String getName() { return name; }
    public LocalDate getBirthDate() { return birthDate; }
} 