package ru.camp.server.model;

import jakarta.persistence.*;
import java.time.LocalDate;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public CampSession getSession() { return session; }
    public void setSession(CampSession session) { this.session = session; }
    public String getBreakfast() { return breakfast; }
    public void setBreakfast(String breakfast) { this.breakfast = breakfast; }
    public String getLunch() { return lunch; }
    public void setLunch(String lunch) { this.lunch = lunch; }
    public String getDinner() { return dinner; }
    public void setDinner(String dinner) { this.dinner = dinner; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
} 