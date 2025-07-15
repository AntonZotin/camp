package ru.camp.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.camp.server.model.MedicalVisit;

public interface MedicalVisitRepository extends JpaRepository<MedicalVisit, Long> {
} 