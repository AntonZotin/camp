package ru.camp.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "menus")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private CampSession session;

    @Column(length = 1000)
    private String breakfast;

    @Column(length = 1000)
    private String lunch;

    @Column(length = 1000)
    private String dinner;

    @Column(length = 1000)
    private String notes;
} 