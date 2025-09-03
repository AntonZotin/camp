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

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private DutyStatus status;

    @Column(length = 2000)
    private String notes;

    @Column(length = 2000)
    private String report;

    @JsonProperty("scheduleId")
    public void setScheduleId(Long scheduleId) {
        if (scheduleId != null) {
            this.schedule = new Schedule();
            this.schedule.setId(scheduleId);
        } else {
            this.schedule = null;
        }
    }
}