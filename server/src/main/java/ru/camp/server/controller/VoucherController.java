package ru.camp.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.aspect.LogActivity;
import ru.camp.server.model.Voucher;
import ru.camp.server.service.VoucherService;
import ru.camp.server.dto.BookVoucherRequest;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {
    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    @LogActivity(action = "VIEW_VOUCHERS", description = "Просмотр списка путёвок")
    public ResponseEntity<List<Voucher>> getAll() {
        return ResponseEntity.ok(voucherService.getAll());
    }

    @GetMapping("/{id}")
    @LogActivity(action = "VIEW_VOUCHER", description = "Просмотр деталей путёвки")
    public ResponseEntity<Voucher> getById(@PathVariable Long id) {
        return ResponseEntity.ok(voucherService.getById(id));
    }

    @PostMapping
    @LogActivity(action = "CREATE_VOUCHER", description = "Создание путёвки")
    public ResponseEntity<Voucher> create(@RequestBody BookVoucherRequest request) {
        try {
            Voucher voucher = voucherService.bookVoucher(request.getChildId(), request.getSessionId());
            return ResponseEntity.ok(voucher);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @LogActivity(action = "UPDATE_VOUCHER", description = "Обновление путёвки")
    public ResponseEntity<Voucher> update(@PathVariable Long id, @RequestBody Voucher voucher) {
        return ResponseEntity.ok(voucherService.update(id, voucher));
    }

    @DeleteMapping("/{id}")
    @LogActivity(action = "DELETE_VOUCHER", description = "Удаление путёвки")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        voucherService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/book")
    @LogActivity(action = "BOOK_VOUCHER", description = "Бронирование путёвки")
    public ResponseEntity<Voucher> bookVoucher(@RequestParam Long childId, @RequestParam Long sessionId) {
        return ResponseEntity.ok(voucherService.bookVoucher(childId, sessionId));
    }

    @GetMapping("/session/{sessionId}/children")
    @LogActivity(action = "VIEW_SESSION_CHILDREN", description = "Просмотр детей по смене")
    public ResponseEntity<List<ru.camp.server.model.Child>> getChildrenBySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(voucherService.getChildrenBySession(sessionId));
    }

    @GetMapping("/parent/{parentId}")
    @LogActivity(action = "VIEW_PARENT_VOUCHERS", description = "Просмотр путёвок родителя")
    public ResponseEntity<List<Voucher>> getByParentId(@PathVariable Long parentId) {
        return ResponseEntity.ok(voucherService.getByParentId(parentId));
    }
} 