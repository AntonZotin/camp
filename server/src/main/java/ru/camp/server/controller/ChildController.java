package ru.camp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.model.Child;
import ru.camp.server.service.ChildService;

import java.util.List;

@RestController
@RequestMapping("/api/children")
public class ChildController {
    private final ChildService childService;

    @Autowired
    public ChildController(ChildService childService) {
        this.childService = childService;
    }

    @GetMapping
    public ResponseEntity<List<Child>> getAll() {
        return ResponseEntity.ok(childService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Child> getById(@PathVariable Long id) {
        return ResponseEntity.ok(childService.getById(id));
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Child>> getByParentId(@PathVariable Long parentId) {
        return ResponseEntity.ok(childService.getByParentId(parentId));
    }

    @PostMapping
    public ResponseEntity<Child> create(@RequestBody Child child) {
        return ResponseEntity.ok(childService.create(child));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Child> update(@PathVariable Long id, @RequestBody Child child) {
        return ResponseEntity.ok(childService.update(id, child));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        childService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 