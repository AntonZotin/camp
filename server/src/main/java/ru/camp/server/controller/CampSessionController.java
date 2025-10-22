package ru.camp.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.aspect.LogActivity;
import ru.camp.server.model.CampSession;
import ru.camp.server.service.CampSessionService;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class CampSessionController {
    private final CampSessionService campSessionService;

    public CampSessionController(CampSessionService campSessionService) {
        this.campSessionService = campSessionService;
    }

    @GetMapping
    @LogActivity(action = "VIEW_SESSIONS", description = "Просмотр списка смен")
    public ResponseEntity<List<CampSession>> getAll() {
        return ResponseEntity.ok(campSessionService.getAll());
    }

    @GetMapping("/{id}")
    @LogActivity(action = "VIEW_SESSION", description = "Просмотр деталей смены")
    public ResponseEntity<CampSession> getById(@PathVariable Long id) {
        return ResponseEntity.ok(campSessionService.getById(id));
    }

    @PostMapping
    @LogActivity(action = "CREATE_SESSION", description = "Создание новой смены")
    public ResponseEntity<CampSession> create(@RequestBody CampSession campSession) {
        return ResponseEntity.ok(campSessionService.create(campSession));
    }

    @PutMapping("/{id}")
    @LogActivity(action = "UPDATE_SESSION", description = "Обновление данных смены")
    public ResponseEntity<CampSession> update(@PathVariable Long id, @RequestBody CampSession campSession) {
        return ResponseEntity.ok(campSessionService.update(id, campSession));
    }

    @DeleteMapping("/{id}")
    @LogActivity(action = "DELETE_SESSION", description = "Удаление смены")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        campSessionService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 