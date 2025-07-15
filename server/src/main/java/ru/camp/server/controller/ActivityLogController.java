package ru.camp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.model.ActivityLog;
import ru.camp.server.service.ActivityLogService;
import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {
    private final ActivityLogService activityLogService;

    @Autowired
    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping
    public ResponseEntity<List<ActivityLog>> getAll() {
        return ResponseEntity.ok(activityLogService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityLog> getById(@PathVariable Long id) {
        return ResponseEntity.ok(activityLogService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ActivityLog> create(@RequestBody ActivityLog log) {
        return ResponseEntity.ok(activityLogService.create(log));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityLog> update(@PathVariable Long id, @RequestBody ActivityLog log) {
        return ResponseEntity.ok(activityLogService.update(id, log));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        activityLogService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 