package ru.camp.server.service;

import org.springframework.stereotype.Service;
import ru.camp.server.model.Notification;
import ru.camp.server.repository.NotificationRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    public NotificationService(NotificationRepository notificationRepository, JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
    }

    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }

    public Notification getById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    public Notification create(Notification notification) {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        notification.setCreatedAt(currentTime);
        if ("email".equalsIgnoreCase(notification.getType())) {
            try {
                sendEmailNotification(notification);
                notification.setStatus("sent");
                notification.setSentAt(currentTime);
            } catch (Exception e) {
                notification.setStatus("failed");
            }
        } else {
            notification.setStatus("pending");
        }
        return notificationRepository.save(notification);
    }

    public Notification update(Long id, Notification notification) {
        notification.setId(id);
        return notificationRepository.save(notification);
    }

    public void delete(Long id) {
        notificationRepository.deleteById(id);
    }

    public void sendEmailNotification(Notification notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.getRecipient().getEmail());
        message.setSubject(notification.getSubject());
        message.setText(notification.getMessage());
        mailSender.send(message);
    }
} 