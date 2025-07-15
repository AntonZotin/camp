package ru.camp.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.camp.server.model.Voucher;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
} 