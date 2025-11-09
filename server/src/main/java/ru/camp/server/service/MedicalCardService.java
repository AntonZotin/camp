package ru.camp.server.service;

import org.springframework.stereotype.Service;
import ru.camp.server.model.MedicalCard;
import ru.camp.server.repository.MedicalCardRepository;
import java.util.List;

@Service
public class MedicalCardService {
    private final MedicalCardRepository medicalCardRepository;

    public MedicalCardService(MedicalCardRepository medicalCardRepository) {
        this.medicalCardRepository = medicalCardRepository;
    }

    public List<MedicalCard> getAll() {
        return medicalCardRepository.findAll();
    }

    public MedicalCard getById(Long id) {
        return medicalCardRepository.findById(id).orElse(null);
    }

    public MedicalCard create(MedicalCard card) {
        return medicalCardRepository.save(card);
    }

    public MedicalCard update(Long id, MedicalCard card) {
        card.setId(id);
        return medicalCardRepository.save(card);
    }

    public void delete(Long id) {
        medicalCardRepository.deleteById(id);
    }

    public MedicalCard getByChildId(Long childId) {
        return medicalCardRepository.findByChild_Id(childId).orElse(null);
    }
} 