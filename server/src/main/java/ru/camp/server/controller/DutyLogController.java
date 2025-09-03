package ru.camp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.model.DutyLog;
import ru.camp.server.service.DutyLogService;
import java.util.List;

@RestController
@RequestMapping("/api/duty-logs")
public class DutyLogController {
    private final DutyLogService dutyLogService;

    @Autowired
    public DutyLogController(DutyLogService dutyLogService) {
        this.dutyLogService = dutyLogService;
    }

    @GetMapping
    public ResponseEntity<List<DutyLog>> getAll() {
        return ResponseEntity.ok(dutyLogService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DutyLog> getById(@PathVariable Long id) {
        return ResponseEntity.ok(dutyLogService.getById(id));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<DutyLog>> getByEmployeeId(@PathVariable Long employeeId) {
        return ResponseEntity.ok(dutyLogService.getByEmployeeId(employeeId));
    }

    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<DutyLog>> getByScheduleId(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(dutyLogService.getByScheduleId(scheduleId));
    }

    @PostMapping("/start-from-schedule/{scheduleId}")
    public ResponseEntity<DutyLog> startFromSchedule(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(dutyLogService.startFromSchedule(scheduleId));
    }

    @PostMapping
    public ResponseEntity<DutyLog> create(@RequestBody DutyLog log) {
        return ResponseEntity.ok(dutyLogService.create(log));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DutyLog> update(@PathVariable Long id, @RequestBody DutyLog log) {
        return ResponseEntity.ok(dutyLogService.update(id, log));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dutyLogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}