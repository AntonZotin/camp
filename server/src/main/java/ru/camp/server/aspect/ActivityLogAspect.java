package ru.camp.server.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.camp.server.model.ActivityLog;
import ru.camp.server.model.User;
import ru.camp.server.service.ActivityLogService;
import ru.camp.server.service.UserService;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Aspect
@Component
public class ActivityLogAspect {

    private final ActivityLogService activityLogService;
    private final UserService userService;

    public ActivityLogAspect(ActivityLogService activityLogService, UserService userService) {
        this.activityLogService = activityLogService;
        this.userService = userService;
    }

    @Around("@annotation(ru.camp.server.aspect.LogActivity)")
    public Object logActivity(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogActivity logAnnotation = method.getAnnotation(LogActivity.class);

        User user = getUserFromRequest(joinPoint, method);

        ActivityLog activityLog = new ActivityLog();
        activityLog.setUser(user);
        activityLog.setAction(logAnnotation.action());
        activityLog.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        String description = logAnnotation.description();
        if (description.isEmpty()) {
            description = generateDescription(joinPoint, method);
        }
        activityLog.setDescription(description);
        activityLogService.create(activityLog);

        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            activityLog.setDescription(activityLog.getDescription() + " (Ошибка: " + e.getMessage() + ")");
            throw e;
        }
    }

    private User getUserFromRequest(ProceedingJoinPoint joinPoint, Method method) {
        if ("login".equals(method.getName())) {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof ru.camp.server.dto.UserLoginRequest loginRequest) {
                String usernameOrEmail = loginRequest.getUsernameOrEmail();
                User user = userService.findByUsername(usernameOrEmail);
                if (user == null) {
                    user = userService.findByEmail(usernameOrEmail);
                }
                return user;
            }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            return userService.findByUsername(username);
        }

        return null;
    }

    private String generateDescription(ProceedingJoinPoint joinPoint, Method method) {
        String methodName = method.getName();
        Object[] args = joinPoint.getArgs();
        return switch (methodName) {
            case "getAll" -> "Просмотр списка";
            case "getById" -> "Просмотр деталей (ID: " + (args.length > 0 ? args[0] : "") + ")";
            case "create" -> "Создание новой записи";
            case "update" -> "Обновление записи (ID: " + (args.length > 0 ? args[0] : "") + ")";
            case "delete" -> "Удаление записи (ID: " + (args.length > 0 ? args[0] : "") + ")";
            case "login" -> {
                if (args.length > 0 && args[0] instanceof ru.camp.server.dto.UserLoginRequest loginRequest) {
                    yield "Вход в систему: " + loginRequest.getUsernameOrEmail();
                }
                yield "Вход в систему";
            }
            case "register" -> {
                if (args.length > 0 && args[0] instanceof ru.camp.server.dto.UserRegistrationRequest registerRequest) {
                    yield "Регистрация пользователя: " + registerRequest.getUsername();
                }
                yield "Регистрация пользователя";
            }
            case "logout" -> "Выход из системы";
            default -> "Выполнение операции: " + methodName;
        };
    }
}