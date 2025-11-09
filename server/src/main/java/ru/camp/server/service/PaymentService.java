package ru.camp.server.service;

import org.springframework.stereotype.Service;
import ru.camp.server.model.Notification;
import ru.camp.server.model.Payment;
import ru.camp.server.model.Voucher;
import ru.camp.server.model.VoucherStatus;
import ru.camp.server.repository.PaymentRepository;
import ru.camp.server.repository.VoucherRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final VoucherRepository voucherRepository;
    private final NotificationService notificationService;

    public PaymentService(PaymentRepository paymentRepository, VoucherRepository voucherRepository, NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.voucherRepository = voucherRepository;
        this.notificationService = notificationService;
    }

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    public Payment getById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public Payment create(Payment payment) {
        Voucher voucher = voucherRepository.findById(payment.getVoucher().getId()).orElse(null);
        if (voucher != null) {
            payment.setDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            Payment paid = paymentRepository.save(payment);
            voucher.setStatus(VoucherStatus.PAID);
            voucherRepository.save(voucher);
            notificationService.create(new Notification(
                payment.getParent(), "email", "Оплата путёвки", "Оплата путёвки прошла успешно"
            ));
            return paid;
        } else {
            throw new RuntimeException("Not found");
        }
    }

    public Payment update(Long id, Payment payment) {
        payment.setId(id);
        return paymentRepository.save(payment);
    }

    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }

    public String generateReceipt(Long paymentId) {
        Payment payment = getById(paymentId);
        if (payment == null) return "Платёж не найден";
        return "Квитанция\n" +
                "Платёж №" + payment.getId() + "\n" +
                "Родитель: " + (payment.getParent() != null ? payment.getParent().getUsername() : "-") + "\n" +
                "Путёвка: " + (payment.getVoucher() != null ? payment.getVoucher().getId() : "-") + "\n" +
                "Сумма: " + payment.getAmount() + " руб.\n" +
                "Дата: " + payment.getDate() + "\n" +
                "Статус: " + payment.getStatus() + "\n" +
                "Способ оплаты: " + payment.getMethod() + "\n" +
                "Комментарий: " + (payment.getComment() != null ? payment.getComment() : "-");
    }
} 