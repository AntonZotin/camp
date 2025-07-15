package ru.camp.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.camp.server.model.Schedule;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByEmployeeId(Long employeeId);
} 