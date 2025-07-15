package ru.camp.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.camp.server.model.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
} 