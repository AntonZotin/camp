package ru.camp.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "medical_visits")
public class MedicalVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "child_id")
    private Child child;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Employee doctor;

    @Column(length = 1000)
    private String description;

    @Column(length = 1000)
    private String recommendations;

    @Column(length = 500)
    private String medications;
} 