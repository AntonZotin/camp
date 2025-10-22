package ru.camp.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.aspect.LogActivity;
import ru.camp.server.model.MedicalVisit;
import ru.camp.server.service.MedicalVisitService;

import java.util.List;

@RestController
@RequestMapping("/api/medical-visits")
public class MedicalVisitController {
    private final MedicalVisitService medicalVisitService;

    public MedicalVisitController(MedicalVisitService medicalVisitService) {
        this.medicalVisitService = medicalVisitService;
    }

    @GetMapping
    @LogActivity(action = "VIEW_MEDICAL_VISITS", description = "Просмотр списка медицинских посещений")
    public ResponseEntity<List<MedicalVisit>> getAll() {
        return ResponseEntity.ok(medicalVisitService.getAll());
    }

    @GetMapping("/{id}")
    @LogActivity(action = "VIEW_MEDICAL_VISIT", description = "Просмотр деталей медицинского посещения")
    public ResponseEntity<MedicalVisit> getById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalVisitService.getById(id));
    }

    @PostMapping
    @LogActivity(action = "CREATE_MEDICAL_VISIT", description = "Создание записи медицинского посещения")
    public ResponseEntity<MedicalVisit> create(@RequestBody MedicalVisit visit) {
        return ResponseEntity.ok(medicalVisitService.create(visit));
    }

    @PutMapping("/{id}")
    @LogActivity(action = "UPDATE_MEDICAL_VISIT", description = "Обновление записи медицинского посещения")
    public ResponseEntity<MedicalVisit> update(@PathVariable Long id, @RequestBody MedicalVisit visit) {
        return ResponseEntity.ok(medicalVisitService.update(id, visit));
    }

    @DeleteMapping("/{id}")
    @LogActivity(action = "DELETE_MEDICAL_VISIT", description = "Удаление записи медицинского посещения")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicalVisitService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 