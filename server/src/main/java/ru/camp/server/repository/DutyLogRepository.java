package ru.camp.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.camp.server.model.DutyLog;

import java.util.List;

public interface DutyLogRepository extends JpaRepository<DutyLog, Long> {
    List<DutyLog> findBySchedule_Employee_Id(Long employeeId);

    List<DutyLog> findBySchedule_Id(Long scheduleId);
}