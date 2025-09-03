package ru.camp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.model.CampSession;
import ru.camp.server.service.CampSessionService;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class CampSessionController {
    private final CampSessionService campSessionService;

    @Autowired
    public CampSessionController(CampSessionService campSessionService) {
        this.campSessionService = campSessionService;
    }

    @GetMapping
    public ResponseEntity<List<CampSession>> getAll() {
        return ResponseEntity.ok(campSessionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampSession> getById(@PathVariable Long id) {
        return ResponseEntity.ok(campSessionService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CampSession> create(@RequestBody CampSession campSession) {
        return ResponseEntity.ok(campSessionService.create(campSession));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampSession> update(@PathVariable Long id, @RequestBody CampSession campSession) {
        return ResponseEntity.ok(campSessionService.update(id, campSession));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        campSessionService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 