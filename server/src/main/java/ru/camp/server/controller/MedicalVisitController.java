package ru.camp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.model.MedicalVisit;
import ru.camp.server.service.MedicalVisitService;
import java.util.List;

@RestController
@RequestMapping("/api/medical-visits")
public class MedicalVisitController {
    private final MedicalVisitService medicalVisitService;

    @Autowired
    public MedicalVisitController(MedicalVisitService medicalVisitService) {
        this.medicalVisitService = medicalVisitService;
    }

    @GetMapping
    public ResponseEntity<List<MedicalVisit>> getAll() {
        return ResponseEntity.ok(medicalVisitService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalVisit> getById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalVisitService.getById(id));
    }

    @PostMapping
    public ResponseEntity<MedicalVisit> create(@RequestBody MedicalVisit visit) {
        return ResponseEntity.ok(medicalVisitService.create(visit));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalVisit> update(@PathVariable Long id, @RequestBody MedicalVisit visit) {
        return ResponseEntity.ok(medicalVisitService.update(id, visit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicalVisitService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 