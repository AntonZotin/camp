package ru.camp.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.camp.server.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
} 