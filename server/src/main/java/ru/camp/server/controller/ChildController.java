package ru.camp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.model.Child;
import ru.camp.server.repository.ChildRepository;
import java.util.List;

@RestController
@RequestMapping("/api/children")
public class ChildController {
    private final ChildRepository childRepository;

    @Autowired
    public ChildController(ChildRepository childRepository) {
        this.childRepository = childRepository;
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Child>> getByParentId(@PathVariable Long parentId) {
        return ResponseEntity.ok(childRepository.findByParentId(parentId));
    }
} 