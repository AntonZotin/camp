package ru.camp.server.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class ChildDto {
    private String name;
    private LocalDate birthDate;
} 