package ru.camp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.dto.UserRegistrationRequest;
import ru.camp.server.dto.UserLoginRequest;
import ru.camp.server.dto.AuthResponse;
import ru.camp.server.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
        try {
            if (request.getUsername() == null || request.getUsername().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank() ||
                request.getEmail() == null || request.getEmail().isBlank()) {
                return ResponseEntity.badRequest().body("Username, email и password обязательны");
            }
            userService.registerUser(request);
            return ResponseEntity.ok().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("Пользователь с таким username или email уже существует");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка регистрации: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        try {
            if (request.getUsernameOrEmail() == null || request.getUsernameOrEmail().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank()) {
                return ResponseEntity.badRequest().body("Username/email и password обязательны");
            }
            AuthResponse response = userService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Неверный логин или пароль");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка входа: " + e.getMessage());
        }
    }
} 