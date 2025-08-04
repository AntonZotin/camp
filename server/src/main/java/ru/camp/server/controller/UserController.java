package ru.camp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.dto.ChangePasswordRequest;
import ru.camp.server.dto.UpdateProfileRequest;
import ru.camp.server.dto.UserSettingsRequest;
import ru.camp.server.model.User;
import ru.camp.server.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            if (user == null) {
                return ResponseEntity.status(404).body("Пользователь не найден");
            }

            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("username", user.getUsername());
            profile.put("email", user.getEmail());
            profile.put("role", user.getRole());

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка получения профиля: " + e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            if (user == null) {
                return ResponseEntity.status(404).body("Пользователь не найден");
            }

            // Проверяем, что новый username не занят другим пользователем
            if (!username.equals(request.getUsername())) {
                User existingUser = userService.findByUsername(request.getUsername());
                if (existingUser != null) {
                    return ResponseEntity.status(409).body("Пользователь с таким логином уже существует");
                }
            }

            // Проверяем, что новый email не занят другим пользователем
            if (!user.getEmail().equals(request.getEmail())) {
                User existingUser = userService.findByEmail(request.getEmail());
                if (existingUser != null) {
                    return ResponseEntity.status(409).body("Пользователь с таким email уже существует");
                }
            }

            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            userService.saveUser(user);

            return ResponseEntity.ok().body("Профиль успешно обновлен");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка обновления профиля: " + e.getMessage());
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            if (user == null) {
                return ResponseEntity.status(404).body("Пользователь не найден");
            }

            // Проверяем текущий пароль
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity.status(400).body("Неверный текущий пароль");
            }

            // Проверяем новый пароль
            if (request.getNewPassword().length() < 6) {
                return ResponseEntity.status(400).body("Новый пароль должен быть не менее 6 символов");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userService.saveUser(user);

            return ResponseEntity.ok().body("Пароль успешно изменен");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка смены пароля: " + e.getMessage());
        }
    }

    @GetMapping("/settings")
    public ResponseEntity<?> getSettings() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            if (user == null) {
                return ResponseEntity.status(404).body("Пользователь не найден");
            }

            Map<String, Object> settings = new HashMap<>();
            settings.put("emailNotifications", user.isEmailNotifications());
            settings.put("smsNotifications", user.isSmsNotifications());
            settings.put("theme", user.getTheme());

            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка получения настроек: " + e.getMessage());
        }
    }

    @PutMapping("/settings")
    public ResponseEntity<?> updateSettings(@RequestBody UserSettingsRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            if (user == null) {
                return ResponseEntity.status(404).body("Пользователь не найден");
            }

            user.setEmailNotifications(request.isEmailNotifications());
            user.setSmsNotifications(request.isSmsNotifications());
            user.setTheme(request.getTheme());
            userService.saveUser(user);

            return ResponseEntity.ok().body("Настройки успешно обновлены");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка обновления настроек: " + e.getMessage());
        }
    }
} 