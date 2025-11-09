package ru.camp.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.camp.server.model.Child;
import ru.camp.server.model.MedicalCard;

import java.util.List;
import java.util.Optional;

public interface MedicalCardRepository extends JpaRepository<MedicalCard, Long> {
    Optional<MedicalCard> findByChild_Id(Long childId);
} 