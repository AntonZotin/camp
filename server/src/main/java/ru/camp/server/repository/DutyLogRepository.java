package ru.camp.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.camp.server.model.DutyLog;

public interface DutyLogRepository extends JpaRepository<DutyLog, Long> {
} 