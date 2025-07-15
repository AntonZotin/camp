package ru.camp.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.camp.server.model.CampSession;

public interface CampSessionRepository extends JpaRepository<CampSession, Long> {
} 