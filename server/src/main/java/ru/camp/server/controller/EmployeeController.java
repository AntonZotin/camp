package ru.camp.server.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.aspect.LogActivity;
import ru.camp.server.model.Employee;
import ru.camp.server.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @LogActivity(action = "VIEW_EMPLOYEES", description = "Просмотр списка сотрудников")
    public ResponseEntity<List<Employee>> getAll() {
        return ResponseEntity.ok(employeeService.getAll());
    }

    @GetMapping("/{id}")
    @LogActivity(action = "VIEW_EMPLOYEE", description = "Просмотр карточки сотрудника")
    public ResponseEntity<Employee> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @PostMapping
    @LogActivity(action = "CREATE_EMPLOYEE", description = "Создание сотрудника")
    public ResponseEntity<?> create(@RequestBody Employee employee) {
        try {
            return ResponseEntity.ok(employeeService.create(employee));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("Пользователь уже связан с сущностью сотрудником");
        }
    }

    @PutMapping("/{id}")
    @LogActivity(action = "UPDATE_EMPLOYEE", description = "Обновление данных сотрудника")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Employee employee) {
        try {
            return ResponseEntity.ok(employeeService.update(id, employee));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("Пользователь уже связан с сущностью сотрудником");
        }
    }

    @DeleteMapping("/{id}")
    @LogActivity(action = "DELETE_EMPLOYEE", description = "Удаление сотрудника")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 