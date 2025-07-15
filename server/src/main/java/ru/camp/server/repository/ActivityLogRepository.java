package ru.camp.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.camp.server.model.ActivityLog;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
} 