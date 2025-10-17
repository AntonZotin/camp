package ru.camp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.aspect.LogActivity;
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
    @LogActivity(action = "VIEW_DUTY_LOGS", description = "Просмотр списка дежурств")
    public ResponseEntity<List<DutyLog>> getAll() {
        return ResponseEntity.ok(dutyLogService.getAll());
    }

    @GetMapping("/{id}")
    @LogActivity(action = "VIEW_DUTY_LOG", description = "Просмотр деталей дежурства")
    public ResponseEntity<DutyLog> getById(@PathVariable Long id) {
        return ResponseEntity.ok(dutyLogService.getById(id));
    }

    @GetMapping("/employee/{employeeId}")
    @LogActivity(action = "VIEW_EMPLOYEE_DUTIES", description = "Просмотр дежурств сотрудника")
    public ResponseEntity<List<DutyLog>> getByEmployeeId(@PathVariable Long employeeId) {
        return ResponseEntity.ok(dutyLogService.getByEmployeeId(employeeId));
    }

    @GetMapping("/schedule/{scheduleId}")
    @LogActivity(action = "VIEW_SCHEDULE_DUTIES", description = "Просмотр дежурств по расписанию")
    public ResponseEntity<List<DutyLog>> getByScheduleId(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(dutyLogService.getByScheduleId(scheduleId));
    }

    @PostMapping("/start-from-schedule/{scheduleId}")
    @LogActivity(action = "START_DUTY", description = "Начало дежурства по расписанию")
    public ResponseEntity<DutyLog> startFromSchedule(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(dutyLogService.startFromSchedule(scheduleId));
    }

    @PostMapping
    @LogActivity(action = "CREATE_DUTY_LOG", description = "Создание записи дежурства")
    public ResponseEntity<DutyLog> create(@RequestBody DutyLog log) {
        return ResponseEntity.ok(dutyLogService.create(log));
    }

    @PutMapping("/{id}")
    @LogActivity(action = "UPDATE_DUTY_LOG", description = "Обновление записи дежурства")
    public ResponseEntity<DutyLog> update(@PathVariable Long id, @RequestBody DutyLog log) {
        return ResponseEntity.ok(dutyLogService.update(id, log));
    }

    @DeleteMapping("/{id}")
    @LogActivity(action = "DELETE_DUTY_LOG", description = "Удаление записи дежурства")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dutyLogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}