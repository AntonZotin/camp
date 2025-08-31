package ru.camp.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.camp.server.model.DutyLog;

import java.util.List;

public interface DutyLogRepository extends JpaRepository<DutyLog, Long> {
    List<DutyLog> findByEmployee_Id(Long employeeId);
} 