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
    public void setChild(Child child) { this.child = child; }
    public void setSession(CampSession session) { this.session = session; }
    public void setStatus(VoucherStatus status) { this.status = status; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
    public Child getChild() { return child; }
    public CampSession getSession() { return session; }
    public VoucherStatus getStatus() { return status; }
    public java.time.LocalDate getBookingDate() { return bookingDate; }
} 