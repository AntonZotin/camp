package ru.camp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.camp.server.model.User;
import ru.camp.server.model.Voucher;
import ru.camp.server.repository.UserRepository;
import ru.camp.server.repository.VoucherRepository;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/export")
public class AdminExportController {
    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;

    @Autowired
    public AdminExportController(UserRepository userRepository, VoucherRepository voucherRepository) {
        this.userRepository = userRepository;
        this.voucherRepository = voucherRepository;
    }

    @GetMapping("/users/csv")
    public ResponseEntity<String> exportUsersToCsv() {
        List<User> users = userRepository.findAll();
        String csv = "id,username,email,role\n" +
                users.stream()
                        .map(u -> u.getId() + "," + u.getUsername() + "," + u.getEmail() + "," + u.getRole())
                        .collect(Collectors.joining("\n"));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csv);
    }

    @GetMapping("/vouchers/csv")
    public ResponseEntity<String> exportVouchersToCsv() {
        List<Voucher> vouchers = voucherRepository.findAll();
        String csv = "id,child_id,session_id,status,booking_date\n" +
                vouchers.stream()
                        .map(v -> v.getId() + "," +
                                (v.getChild() != null ? v.getChild().getId() : "") + "," +
                                (v.getSession() != null ? v.getSession().getId() : "") + "," +
                                v.getStatus() + "," + v.getBookingDate())
                        .collect(Collectors.joining("\n"));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vouchers.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csv);
    }
} 