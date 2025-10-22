package ru.camp.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.aspect.LogActivity;
import ru.camp.server.model.Child;
import ru.camp.server.service.ChildService;

import java.util.List;

@RestController
@RequestMapping("/api/children")
public class ChildController {
    private final ChildService childService;

    public ChildController(ChildService childService) {
        this.childService = childService;
    }

    @GetMapping
    @LogActivity(action = "VIEW_CHILDREN_LIST", description = "Просмотр списка детей")
    public ResponseEntity<List<Child>> getAll() {
        return ResponseEntity.ok(childService.getAll());
    }

    @GetMapping("/{id}")
    @LogActivity(action = "VIEW_CHILD", description = "Просмотр карточки ребёнка")
    public ResponseEntity<Child> getById(@PathVariable Long id) {
        return ResponseEntity.ok(childService.getById(id));
    }

    @GetMapping("/parent/{parentId}")
    @LogActivity(action = "VIEW_PARENT_CHILDREN", description = "Просмотр детей родителя")
    public ResponseEntity<List<Child>> getByParentId(@PathVariable Long parentId) {
        return ResponseEntity.ok(childService.getByParentId(parentId));
    }

    @PostMapping
    @LogActivity(action = "REGISTER_CHILD", description = "Регистрация ребёнка")
    public ResponseEntity<Child> create(@RequestBody Child child) {
        return ResponseEntity.ok(childService.create(child));
    }

    @PutMapping("/{id}")
    @LogActivity(action = "UPDATE_CHILD", description = "Обновление данных ребёнка")
    public ResponseEntity<Child> update(@PathVariable Long id, @RequestBody Child child) {
        return ResponseEntity.ok(childService.update(id, child));
    }

    @DeleteMapping("/{id}")
    @LogActivity(action = "DELETE_CHILD", description = "Удаление ребёнка")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        childService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 