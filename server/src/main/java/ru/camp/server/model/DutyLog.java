package ru.camp.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@Entity
@Table(name = "duty_logs")
public class DutyLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private CampSession session;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false, length = 100)
    private String location;

    @Enumerated(EnumType.STRING)
    private DutyStatus status;

    @Column(length = 2000)
    private String description;

    @Column(length = 2000)
    private String notes;

    @Column(length = 2000)
    private String report;

    @JsonProperty("employeeId")
    public void setEmployeeId(Long employeeId) {
        if (employeeId != null) {
            this.employee = new Employee();
            this.employee.setId(employeeId);
        } else {
            this.employee = null;
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