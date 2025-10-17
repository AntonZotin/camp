package ru.camp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.aspect.LogActivity;
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
    @LogActivity(action = "VIEW_MEDICAL_CARDS", description = "Просмотр списка медицинских карт")
    public ResponseEntity<List<MedicalCard>> getAll() {
        return ResponseEntity.ok(medicalCardService.getAll());
    }

    @GetMapping("/{id}")
    @LogActivity(action = "VIEW_MEDICAL_CARD", description = "Просмотр медицинской карты")
    public ResponseEntity<MedicalCard> getById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalCardService.getById(id));
    }

    @PostMapping
    @LogActivity(action = "CREATE_MEDICAL_CARD", description = "Создание медицинской карты")
    public ResponseEntity<?> create(@RequestBody MedicalCard card) {
        try {
            return ResponseEntity.ok(medicalCardService.create(card));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("Этот ребёнок уже имеет медицинскую карту");
        }
    }

    @PutMapping("/{id}")
    @LogActivity(action = "UPDATE_MEDICAL_CARD", description = "Обновление медицинской карты")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody MedicalCard card) {
        try {
            return ResponseEntity.ok(medicalCardService.update(id, card));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("Этот ребёнок уже имеет медицинскую карту");
        }
    }

    @DeleteMapping("/{id}")
    @LogActivity(action = "DELETE_MEDICAL_CARD", description = "Удаление медицинской карты")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicalCardService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 