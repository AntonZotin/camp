package ru.camp.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.camp.server.model.Voucher;
import ru.camp.server.repository.VoucherRepository;
import ru.camp.server.repository.ChildRepository;
import ru.camp.server.repository.CampSessionRepository;
import ru.camp.server.model.Child;
import ru.camp.server.model.CampSession;
import ru.camp.server.model.VoucherStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoucherService {
    private final VoucherRepository voucherRepository;
    private final ChildRepository childRepository;
    private final CampSessionRepository campSessionRepository;

    @Autowired
    public VoucherService(VoucherRepository voucherRepository, ChildRepository childRepository, CampSessionRepository campSessionRepository) {
        this.voucherRepository = voucherRepository;
        this.childRepository = childRepository;
        this.campSessionRepository = campSessionRepository;
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

    public Voucher bookVoucher(Long childId, Long sessionId) {
        Child child = childRepository.findById(childId).orElseThrow(() -> new RuntimeException("Child not found"));
        CampSession session = campSessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));
        Voucher voucher = new Voucher();
        voucher.setChild(child);
        voucher.setSession(session);
        voucher.setStatus(VoucherStatus.BOOKED);
        voucher.setBookingDate(LocalDate.now());
        return voucherRepository.save(voucher);
    }

    public List<Child> getChildrenBySession(Long sessionId) {
        return voucherRepository.findAll().stream()
            .filter(v -> v.getSession() != null && v.getSession().getId().equals(sessionId))
            .map(Voucher::getChild)
            .distinct()
            .collect(Collectors.toList());
    }

    public List<Voucher> getByParentId(Long parentId) {
        return voucherRepository.findByChildParentId(parentId);
    }
} 