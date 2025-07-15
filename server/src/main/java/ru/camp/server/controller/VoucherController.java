package ru.camp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.model.Voucher;
import ru.camp.server.service.VoucherService;
import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {
    private final VoucherService voucherService;

    @Autowired
    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public ResponseEntity<List<Voucher>> getAll() {
        return ResponseEntity.ok(voucherService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voucher> getById(@PathVariable Long id) {
        return ResponseEntity.ok(voucherService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Voucher> create(@RequestBody Voucher voucher) {
        return ResponseEntity.ok(voucherService.create(voucher));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Voucher> update(@PathVariable Long id, @RequestBody Voucher voucher) {
        return ResponseEntity.ok(voucherService.update(id, voucher));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        voucherService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/book")
    public ResponseEntity<Voucher> bookVoucher(@RequestParam Long childId, @RequestParam Long sessionId) {
        return ResponseEntity.ok(voucherService.bookVoucher(childId, sessionId));
    }

    @GetMapping("/session/{sessionId}/children")
    public ResponseEntity<List<ru.camp.server.model.Child>> getChildrenBySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(voucherService.getChildrenBySession(sessionId));
    }
} 