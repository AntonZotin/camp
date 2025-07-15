package ru.camp.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.camp.server.model.Voucher;
import ru.camp.server.repository.VoucherRepository;
import java.util.List;

@Service
public class VoucherService {
    private final VoucherRepository voucherRepository;

    @Autowired
    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    public List<Voucher> getAll() {
        return voucherRepository.findAll();
    }

    public Voucher getById(Long id) {
        return voucherRepository.findById(id).orElse(null);
    }

    public Voucher create(Voucher voucher) {
        return voucherRepository.save(voucher);
    }

    public Voucher update(Long id, Voucher voucher) {
        voucher.setId(id);
        return voucherRepository.save(voucher);
    }

    public void delete(Long id) {
        voucherRepository.deleteById(id);
    }
} 