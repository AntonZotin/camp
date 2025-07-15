package ru.camp.server.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "child_id")
    private Child child;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private CampSession session;

    @Enumerated(EnumType.STRING)
    private VoucherStatus status;

    private LocalDate bookingDate;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
} 