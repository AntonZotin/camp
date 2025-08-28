package ru.camp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.model.MedicalCard;
import ru.camp.server.service.MedicalCardService;
import java.util.List;

@RestController
@RequestMapping("/api/medical-cards")
public class MedicalCardController {
    private final MedicalCardService medicalCardService;

    @Autowired
    public MedicalCardController(MedicalCardService medicalCardService) {
        this.medicalCardService = medicalCardService;
    }

    @GetMapping
    public ResponseEntity<List<MedicalCard>> getAll() {
        return ResponseEntity.ok(medicalCardService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalCard> getById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalCardService.getById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MedicalCard card) {
        try {
            return ResponseEntity.ok(medicalCardService.create(card));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("Этот ребёнок уже имеет медицинскую карту");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody MedicalCard card) {
        try {
            return ResponseEntity.ok(medicalCardService.update(id, card));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("Этот ребёнок уже имеет медицинскую карту");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicalCardService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 