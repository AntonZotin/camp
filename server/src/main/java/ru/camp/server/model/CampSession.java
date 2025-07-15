package ru.camp.server.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "camp_sessions")
public class CampSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private String description;

    // Геттеры и сеттеры
    // ...
} 