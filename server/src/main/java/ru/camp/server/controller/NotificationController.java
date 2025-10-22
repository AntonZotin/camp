package ru.camp.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.aspect.LogActivity;
import ru.camp.server.model.Notification;
import ru.camp.server.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @LogActivity(action = "VIEW_NOTIFICATIONS", description = "Просмотр списка уведомлений")
    public ResponseEntity<List<Notification>> getAll() {
        return ResponseEntity.ok(notificationService.getAll());
    }

    @GetMapping("/{id}")
    @LogActivity(action = "VIEW_NOTIFICATION", description = "Просмотр уведомления")
    public ResponseEntity<Notification> getById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getById(id));
    }

    @PostMapping
    @LogActivity(action = "CREATE_NOTIFICATION", description = "Создание уведомления")
    public ResponseEntity<Notification> create(@RequestBody Notification notification) {
        return ResponseEntity.ok(notificationService.create(notification));
    }

    @PutMapping("/{id}")
    @LogActivity(action = "UPDATE_NOTIFICATION", description = "Обновление уведомления")
    public ResponseEntity<Notification> update(@PathVariable Long id, @RequestBody Notification notification) {
        return ResponseEntity.ok(notificationService.update(id, notification));
    }

    @DeleteMapping("/{id}")
    @LogActivity(action = "DELETE_NOTIFICATION", description = "Удаление уведомления")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 