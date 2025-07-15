package ru.camp.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.camp.server.model.Child;

public interface ChildRepository extends JpaRepository<Child, Long> {
} 