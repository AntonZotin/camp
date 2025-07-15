package ru.camp.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.camp.server.model.MedicalCard;

public interface MedicalCardRepository extends JpaRepository<MedicalCard, Long> {
} 