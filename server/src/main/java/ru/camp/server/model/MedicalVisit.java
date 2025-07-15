package ru.camp.server.model;

import jakarta.persistence.*;
import java.time.LocalDate;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Child getChild() { return child; }
    public void setChild(Child child) { this.child = child; }
    public Employee getDoctor() { return doctor; }
    public void setDoctor(Employee doctor) { this.doctor = doctor; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    public String getMedications() { return medications; }
    public void setMedications(String medications) { this.medications = medications; }
} 