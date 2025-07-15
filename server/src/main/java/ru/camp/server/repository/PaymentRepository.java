package ru.camp.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.camp.server.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
} 