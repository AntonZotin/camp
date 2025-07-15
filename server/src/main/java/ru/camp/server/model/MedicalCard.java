package ru.camp.server.model;

import jakarta.persistence.*;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Child getChild() { return child; }
    public void setChild(Child child) { this.child = child; }
    public String getHealthInfo() { return healthInfo; }
    public void setHealthInfo(String healthInfo) { this.healthInfo = healthInfo; }
    public String getChronicDiseases() { return chronicDiseases; }
    public void setChronicDiseases(String chronicDiseases) { this.chronicDiseases = chronicDiseases; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public String getVaccinations() { return vaccinations; }
    public void setVaccinations(String vaccinations) { this.vaccinations = vaccinations; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
} 