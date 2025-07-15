package ru.camp.server.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "children")
public class Child {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private User parent;

    // Геттеры и сеттеры
    public void setName(String name) { this.name = name; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public void setParent(User parent) { this.parent = parent; }
} 