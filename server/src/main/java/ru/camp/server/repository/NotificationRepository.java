package ru.camp.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.camp.server.model.DutyLog;
import ru.camp.server.model.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient_Id(Long recipientId);
} 