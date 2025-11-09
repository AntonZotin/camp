package ru.camp.server.service;

import org.springframework.stereotype.Service;
import ru.camp.server.model.Child;
import ru.camp.server.model.MedicalVisit;
import ru.camp.server.model.Notification;
import ru.camp.server.model.User;
import ru.camp.server.repository.MedicalVisitRepository;
import java.util.List;

@Service
public class MedicalVisitService {
    private final MedicalVisitRepository medicalVisitRepository;
    private final NotificationService notificationService;
    private final ChildService childService;

    public MedicalVisitService(MedicalVisitRepository medicalVisitRepository, NotificationService notificationService, ChildService childService) {
        this.medicalVisitRepository = medicalVisitRepository;
        this.notificationService = notificationService;
        this.childService = childService;
    }

    public List<MedicalVisit> getAll() {
        return medicalVisitRepository.findAll();
    }

    public MedicalVisit getById(Long id) {
        return medicalVisitRepository.findById(id).orElse(null);
    }

    public MedicalVisit create(MedicalVisit visit) {
        MedicalVisit medicalVisit = medicalVisitRepository.save(visit);
        Child child = childService.getById(visit.getChild().getId());
        notificationService.create(new Notification(
            child.getParent(), "email", "Медицинский осмотр", "Вашего ребёнка осмотрел врач"
        ));
        return medicalVisit;
    }

    public MedicalVisit update(Long id, MedicalVisit visit) {
        visit.setId(id);
        return medicalVisitRepository.save(visit);
    }

    public void delete(Long id) {
        medicalVisitRepository.deleteById(id);
    }
} 