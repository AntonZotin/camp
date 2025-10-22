package ru.camp.server.service;

import org.springframework.stereotype.Service;
import ru.camp.server.model.Payment;
import ru.camp.server.repository.PaymentRepository;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    public Payment getById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public Payment create(Payment payment) {
        return paymentRepository.save(payment);
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