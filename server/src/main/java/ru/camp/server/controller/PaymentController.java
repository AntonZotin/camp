package ru.camp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.aspect.LogActivity;
import ru.camp.server.model.Payment;
import ru.camp.server.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    @LogActivity(action = "VIEW_PAYMENTS", description = "Просмотр списка платежей")
    public ResponseEntity<List<Payment>> getAll() {
        return ResponseEntity.ok(paymentService.getAll());
    }

    @GetMapping("/{id}")
    @LogActivity(action = "VIEW_PAYMENT", description = "Просмотр деталей платежа")
    public ResponseEntity<Payment> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }

    @GetMapping("/{id}/receipt")
    @LogActivity(action = "GENERATE_RECEIPT", description = "Генерация квитанции")
    public ResponseEntity<String> getReceipt(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.generateReceipt(id));
    }

    @PostMapping
    @LogActivity(action = "CREATE_PAYMENT", description = "Создание платежа")
    public ResponseEntity<Payment> create(@RequestBody Payment payment) {
        return ResponseEntity.ok(paymentService.create(payment));
    }

    @PutMapping("/{id}")
    @LogActivity(action = "UPDATE_PAYMENT", description = "Обновление платежа")
    public ResponseEntity<Payment> update(@PathVariable Long id, @RequestBody Payment payment) {
        return ResponseEntity.ok(paymentService.update(id, payment));
    }

    @DeleteMapping("/{id}")
    @LogActivity(action = "DELETE_PAYMENT", description = "Удаление платежа")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 