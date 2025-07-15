package ru.camp.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.camp.server.model.MedicalVisit;
import ru.camp.server.repository.MedicalVisitRepository;
import java.util.List;

@Service
public class MedicalVisitService {
    private final MedicalVisitRepository medicalVisitRepository;

    @Autowired
    public MedicalVisitService(MedicalVisitRepository medicalVisitRepository) {
        this.medicalVisitRepository = medicalVisitRepository;
    }

    public List<MedicalVisit> getAll() {
        return medicalVisitRepository.findAll();
    }

    public MedicalVisit getById(Long id) {
        return medicalVisitRepository.findById(id).orElse(null);
    }

    public MedicalVisit create(MedicalVisit visit) {
        return medicalVisitRepository.save(visit);
    }

    public MedicalVisit update(Long id, MedicalVisit visit) {
        visit.setId(id);
        return medicalVisitRepository.save(visit);
    }

    public void delete(Long id) {
        medicalVisitRepository.deleteById(id);
    }
} 