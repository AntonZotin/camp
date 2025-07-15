package ru.camp.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.camp.server.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
} 