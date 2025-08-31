package ru.camp.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
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

    @JsonProperty("childId")
    public void setChildId(Long childId) {
        if (childId != null) {
            this.child = new Child();
            this.child.setId(childId);
        } else {
            this.child = null;
        }
    }

    @JsonProperty("sessionId")
    public void setSessionId(Long sessionId) {
        if (sessionId != null) {
            this.session = new CampSession();
            this.session.setId(sessionId);
        } else {
            this.session = null;
        }
    }
}