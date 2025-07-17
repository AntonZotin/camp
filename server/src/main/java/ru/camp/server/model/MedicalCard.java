package ru.camp.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "medical_cards")
public class MedicalCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "child_id", unique = true)
    private Child child;

    @Column(length = 1000)
    private String healthInfo;

    @Column(length = 500)
    private String chronicDiseases;

    @Column(length = 500)
    private String allergies;

    @Column(length = 500)
    private String vaccinations;

    @Column(length = 1000)
    private String notes;
} 