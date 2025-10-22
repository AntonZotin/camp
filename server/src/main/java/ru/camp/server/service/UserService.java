package ru.camp.server.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.camp.server.dto.UserRegistrationRequest;
import ru.camp.server.model.*;
import ru.camp.server.repository.UserRepository;
import ru.camp.server.dto.UserLoginRequest;
import ru.camp.server.dto.AuthResponse;
import ru.camp.server.config.JwtUtil;
import ru.camp.server.repository.ChildRepository;
import ru.camp.server.repository.EmployeeRepository;
import ru.camp.server.dto.ChildDto;
import ru.camp.server.dto.ForgotPasswordRequest;
import ru.camp.server.dto.ResetPasswordRequest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ChildRepository childRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;
    private final ConcurrentHashMap<String, String> resetTokens = new ConcurrentHashMap<>();

    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, ChildRepository childRepository, EmployeeRepository employeeRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.childRepository = childRepository;
        this.employeeRepository = employeeRepository;
        this.notificationService = notificationService;
    }

    public User registerUser(UserRegistrationRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        if (request.getRole() == Role.PARENT && request.getChildren() != null) {
            HashSet<Child> children = new HashSet<>();
            for (ChildDto childDto : request.getChildren()) {
                Child child = new Child();
                child.setFullName(childDto.getFullName());
                child.setBirthDate(childDto.getBirthDate());
                child.setParent(user);
                children.add(child);
            }
            user.setChildren(children);
        }
        if (request.getRole() == Role.EMPLOYEE) {
            Employee employee = new Employee();
            employee.setFullName(request.getEmployee().getFullName());
            employee.setPosition(request.getEmployee().getPosition());
            employee.setUser(user);
            user.setEmployee(employee);
        }
        User savedUser = userRepository.save(user);
        if (savedUser.getChildren() != null) {
            childRepository.saveAll(savedUser.getChildren());
        }
        if (savedUser.getEmployee() != null) {
            employeeRepository.save(savedUser.getEmployee());
        }
        return user;
    }

    public AuthResponse authenticate(UserLoginRequest request) {
        User user = userRepository.findByUsername(request.getUsernameOrEmail()).or(() -> userRepository.findByEmail(request.getUsernameOrEmail())).orElse(null);
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        AuthResponse response = new AuthResponse();
        response.setAccessToken(token);
        response.setUserId(user.getId());
        response.setRole(user.getRole());
        return response;
    }

    public void processForgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            throw new RuntimeException("Пользователь с таким email не найден");
        }
        String token = UUID.randomUUID().toString();
        resetTokens.put(user.getEmail(), token);
        String resetLink = frontendUrl + "/reset-password?token=" + token;
        Notification notification = new Notification();
        notification.setRecipient(user);
        notification.setType("email");
        notification.setSubject("Восстановление пароля Sunny Camp");
        notification.setMessage("Здравствуйте!\n\nВы запросили восстановление пароля. Перейдите по ссылке для сброса пароля: " + resetLink + "\n\nЕсли это были не вы, проигнорируйте это письмо.");
        notificationService.create(notification);
    }

    public void resetPassword(ResetPasswordRequest request) {
        String email = resetTokens.entrySet().stream()
            .filter(e -> e.getValue().equals(request.getToken()))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);
        if (email == null) {
            throw new RuntimeException("Недействительный или истекший токен");
        }
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        resetTokens.remove(email);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User update(Long id, User user) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        existingUser.setUsername(user.getUsername());
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        existingUser.setEmail(user.getEmail());
        existingUser.setRole(user.getRole());

        if (user.getEmployee() != null) {
            if (existingUser.getEmployee() == null) {
                Employee employee = user.getEmployee();
                employee.setUser(existingUser);
                existingUser.setEmployee(employee);
            } else {
                existingUser.getEmployee().setFullName(user.getEmployee().getFullName());
                existingUser.getEmployee().setPosition(user.getEmployee().getPosition());
            }
        } else {
            if (existingUser.getEmployee() != null) {
                existingUser.setEmployee(null);
            }
        }

        if (user.getChildren() != null) {
            Set<Child> childrenToRemove = new HashSet<>();
            for (Child existingChild : existingUser.getChildren()) {
                boolean found = user.getChildren().stream()
                    .anyMatch(c -> c.getId() != null && c.getId().equals(existingChild.getId()));
                if (!found) {
                    childrenToRemove.add(existingChild);
                }
            }
            existingUser.getChildren().removeAll(childrenToRemove);

            for (Child child : user.getChildren()) {
                if (child.getId() == null) {
                    child.setParent(existingUser);
                    existingUser.getChildren().add(child);
                } else {
                    Optional<Child> existingChildOpt = existingUser.getChildren().stream()
                        .filter(c -> c.getId().equals(child.getId()))
                        .findFirst();
                    if (existingChildOpt.isPresent()) {
                        Child existingChild = existingChildOpt.get();
                        existingChild.setFullName(child.getFullName());
                        existingChild.setBirthDate(child.getBirthDate());
                    }
                }
            }
        } else {
            existingUser.getChildren().clear();
        }

        return saveUser(existingUser);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
} 