package ru.camp.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.camp.server.model.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {
} 