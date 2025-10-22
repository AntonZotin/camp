package ru.camp.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.aspect.LogActivity;
import ru.camp.server.dto.UserRegistrationRequest;
import ru.camp.server.dto.UserLoginRequest;
import ru.camp.server.dto.AuthResponse;
import ru.camp.server.model.User;
import ru.camp.server.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import ru.camp.server.dto.ForgotPasswordRequest;
import ru.camp.server.dto.ResetPasswordRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @LogActivity(action = "REGISTER", description = "Регистрация нового пользователя")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
        try {
            if (request.getUsername() == null || request.getUsername().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank() ||
                request.getEmail() == null || request.getEmail().isBlank()) {
                return ResponseEntity.badRequest().body("Username, email и password обязательны");
            }
            User user = userService.registerUser(request);
            return ResponseEntity.ok(user);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("Пользователь с таким username или email уже существует");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка регистрации: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    @LogActivity(action = "LOGIN", description = "Вход в систему")
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

    @PostMapping("/logout")
    @LogActivity(action = "LOGOUT", description = "Выход из системы")
    public ResponseEntity<?> logout() {
        try {
            return ResponseEntity.ok().body("Успешный выход из системы");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка выхода: " + e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    @LogActivity(action = "FORGOT_PASSWORD", description = "Запрос восстановления пароля")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            userService.processForgotPassword(request);
            return ResponseEntity.ok().body("Инструкции по восстановлению пароля отправлены на email, если он зарегистрирован.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка восстановления пароля: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    @LogActivity(action = "RESET_PASSWORD", description = "Сброс пароля")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request);
            return ResponseEntity.ok().body("Пароль успешно сброшен");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка сброса пароля: " + e.getMessage());
        }
    }
} 