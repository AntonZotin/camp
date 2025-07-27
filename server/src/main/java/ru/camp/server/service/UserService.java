package ru.camp.server.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.camp.server.dto.UserRegistrationRequest;
import ru.camp.server.model.User;
import ru.camp.server.repository.UserRepository;
import ru.camp.server.dto.UserLoginRequest;
import ru.camp.server.dto.AuthResponse;
import ru.camp.server.config.JwtUtil;
import ru.camp.server.repository.ChildRepository;
import ru.camp.server.repository.EmployeeRepository;
import ru.camp.server.model.Child;
import ru.camp.server.model.Employee;
import ru.camp.server.dto.ChildDto;
import ru.camp.server.dto.ForgotPasswordRequest;
import ru.camp.server.model.Notification;
import ru.camp.server.dto.ResetPasswordRequest;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;

import ru.camp.server.model.UserType;
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

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, ChildRepository childRepository, EmployeeRepository employeeRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.childRepository = childRepository;
        this.employeeRepository = employeeRepository;
        this.notificationService = notificationService;
    }

    public void registerUser(UserRegistrationRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setUserType(request.getUserType());
        if (request.getUserType() == UserType.PARENT && request.getChildren() != null) {
            HashSet<Child> children = new HashSet<>();
            for (ChildDto childDto : request.getChildren()) {
                Child child = new Child();
                child.setName(childDto.getName());
                child.setBirthDate(childDto.getBirthDate());
                child.setParent(user);
                children.add(child);
            }
            user.setChildren(children);
        }
        if (request.getUserType() == UserType.EMPLOYEE) {
            Employee employee = new Employee();
            employee.setFullName(request.getFullName());
            employee.setPosition(request.getPosition());
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
} 