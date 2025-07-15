package ru.camp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.camp.server.model.User;
import ru.camp.server.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/export")
public class AdminExportController {
    private final UserRepository userRepository;

    @Autowired
    public AdminExportController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
} 