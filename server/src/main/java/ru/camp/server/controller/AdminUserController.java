package ru.camp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.model.User;
import ru.camp.server.model.Role;
import ru.camp.server.repository.UserRepository;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final UserRepository userRepository;

    @Autowired
    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteMany(@RequestBody List<Long> ids) {
        ids.forEach(userRepository::deleteById);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<User> updateRole(@PathVariable Long id, @RequestParam Role role) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setRole(role);
                    userRepository.save(user);
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/roles")
    public ResponseEntity<Void> updateRoles(@RequestBody List<UserRoleUpdateRequest> updates) {
        updates.forEach(update -> userRepository.findById(update.getUserId()).ifPresent(user -> {
            user.setRole(update.getRole());
            userRepository.save(user);
        }));
        return ResponseEntity.ok().build();
    }

    public static class UserRoleUpdateRequest {
        private Long userId;
        private Role role;
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
    }
} 