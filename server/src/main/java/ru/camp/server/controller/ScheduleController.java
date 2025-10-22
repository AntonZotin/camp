package ru.camp.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.aspect.LogActivity;
import ru.camp.server.model.Schedule;
import ru.camp.server.service.ScheduleService;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    @LogActivity(action = "VIEW_SCHEDULES", description = "Просмотр списка расписаний")
    public ResponseEntity<List<Schedule>> getAll() {
        return ResponseEntity.ok(scheduleService.getAll());
    }

    @GetMapping("/{id}")
    @LogActivity(action = "VIEW_SCHEDULE", description = "Просмотр деталей расписания")
    public ResponseEntity<Schedule> getById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getById(id));
    }

    @GetMapping("/employee/{employeeId}")
    @LogActivity(action = "VIEW_EMPLOYEE_SCHEDULE", description = "Просмотр расписания сотрудника")
    public ResponseEntity<List<Schedule>> getByEmployeeId(@PathVariable Long employeeId) {
        return ResponseEntity.ok(scheduleService.getByEmployeeId(employeeId));
    }

    @PostMapping
    @LogActivity(action = "CREATE_SCHEDULE", description = "Создание расписания")
    public ResponseEntity<Schedule> create(@RequestBody Schedule schedule) {
        return ResponseEntity.ok(scheduleService.create(schedule));
    }

    @PutMapping("/{id}")
    @LogActivity(action = "UPDATE_SCHEDULE", description = "Обновление расписания")
    public ResponseEntity<Schedule> update(@PathVariable Long id, @RequestBody Schedule schedule) {
        return ResponseEntity.ok(scheduleService.update(id, schedule));
    }

    @DeleteMapping("/{id}")
    @LogActivity(action = "DELETE_SCHEDULE", description = "Удаление расписания")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 