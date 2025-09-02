package ru.camp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.camp.server.model.*;
import ru.camp.server.repository.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/export")
public class AdminExportController {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;
    private final CampSessionRepository campSessionRepository;
    private final ChildRepository childRepository;
    private final DutyLogRepository dutyLogRepository;
    private final EmployeeRepository employeeRepository;
    private final MedicalCardRepository medicalCardRepository;
    private final MedicalVisitRepository medicalVisitRepository;
    private final MenuRepository menuRepository;
    private final NotificationRepository notificationRepository;
    private final PaymentRepository paymentRepository;
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public AdminExportController(ActivityLogRepository activityLogRepository,
                                 UserRepository userRepository,
                                 VoucherRepository voucherRepository,
                                 CampSessionRepository campSessionRepository,
                                 ChildRepository childRepository,
                                 DutyLogRepository dutyLogRepository,
                                 EmployeeRepository employeeRepository,
                                 MedicalCardRepository medicalCardRepository,
                                 MedicalVisitRepository medicalVisitRepository,
                                 MenuRepository menuRepository,
                                 NotificationRepository notificationRepository,
                                 PaymentRepository paymentRepository,
                                 ScheduleRepository scheduleRepository) {
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
        this.voucherRepository = voucherRepository;
        this.campSessionRepository = campSessionRepository;
        this.childRepository = childRepository;
        this.dutyLogRepository = dutyLogRepository;
        this.employeeRepository = employeeRepository;
        this.medicalCardRepository = medicalCardRepository;
        this.medicalVisitRepository = medicalVisitRepository;
        this.menuRepository = menuRepository;
        this.notificationRepository = notificationRepository;
        this.paymentRepository = paymentRepository;
        this.scheduleRepository = scheduleRepository;
    }

    private <T> ResponseEntity<String> exportToCsv(String filename, String header, List<T> items, Function<T, String> mapFunction) {
        String csv = header + "\n" +
            items.stream()
                .map(mapFunction)
                .collect(Collectors.joining("\n"));
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE + "; charset=" + StandardCharsets.UTF_8.name())
            .body(csv);
    }

    @GetMapping("/activity-logs/csv")
    public ResponseEntity<String> exportActivityLogsToCsv() {
        List<ActivityLog> activityLogs = activityLogRepository.findAll();
        return exportToCsv("activity-logs.csv", "id,user_id,action,timestamp,description",
            activityLogs, a -> a.getId() + "," +
                (a.getUser() != null ? a.getUser().getId() : "") + "," +
                a.getAction() + "," +
                a.getTimestamp() + "," +
                a.getDescription());
    }

    @GetMapping("/camp-sessions/csv")
    public ResponseEntity<String> exportCampSessionsToCsv() {
        List<CampSession> campSessions = campSessionRepository.findAll();
        return exportToCsv("camp-sessions.csv", "id,name,start_date,end_date,description,max_children,price",
            campSessions, c -> c.getId() + "," +
                c.getName() + "," +
                c.getStartDate() + "," +
                c.getEndDate() + "," +
                c.getDescription() + "," +
                c.getMaxChildren() + "," +
                c.getPrice());
    }

    @GetMapping("/children/csv")
    public ResponseEntity<String> exportChildrenToCsv() {
        List<Child> children = childRepository.findAll();
        return exportToCsv("children.csv", "id,full_name,birth_date,parent_id,parent_username",
            children, c -> c.getId() + "," +
                c.getFullName() + "," +
                c.getBirthDate() + "," +
                (c.getParent() != null ? c.getParent().getId() : "") + "," +
                (c.getParent() != null ? c.getParent().getUsername() : ""));
    }

    @GetMapping("/duty-logs/csv")
    public ResponseEntity<String> exportDutyLogsToCsv() {
        List<DutyLog> dutyLogs = dutyLogRepository.findAll();
        return exportToCsv("duty-logs.csv", "id,date,employee_id,session_id,start_time,end_time,location,status,description,notes,report",
            dutyLogs, d -> d.getId() + "," +
                d.getDate() + "," +
                (d.getEmployee() != null ? d.getEmployee().getId() : "") + "," +
                (d.getSession() != null ? d.getSession().getId() : "") + "," +
                d.getStartTime() + "," +
                d.getEndTime() + "," +
                d.getLocation() + "," +
                d.getStatus() + "," +
                d.getDescription() + "," +
                d.getNotes() + "," +
                d.getReport());
    }

    @GetMapping("/employees/csv")
    public ResponseEntity<String> exportEmployeesToCsv() {
        List<Employee> employees = employeeRepository.findAll();
        return exportToCsv("employees.csv", "id,full_name,position,user_id,username",
            employees, e -> e.getId() + "," +
                e.getFullName() + "," +
                e.getPosition() + "," +
                (e.getUser() != null ? e.getUser().getId() : "") + "," +
                (e.getUser() != null ? e.getUser().getUsername() : ""));
    }

    @GetMapping("/medical-cards/csv")
    public ResponseEntity<String> exportMedicalCardsToCsv() {
        List<MedicalCard> medicalCards = medicalCardRepository.findAll();
        return exportToCsv("medical-cards.csv", "id,child_id,health_info,chronic_diseases,allergies,vaccinations,notes",
            medicalCards, m -> m.getId() + "," +
                (m.getChild() != null ? m.getChild().getId() : "") + "," +
                m.getHealthInfo() + "," +
                m.getChronicDiseases() + "," +
                m.getAllergies() + "," +
                m.getVaccinations() + "," +
                m.getNotes());
    }

    @GetMapping("/medical-visits/csv")
    public ResponseEntity<String> exportMedicalVisitsToCsv() {
        List<MedicalVisit> medicalVisits = medicalVisitRepository.findAll();
        return exportToCsv("medical-visits.csv", "id,date,child_id,doctor_id,description,recommendations,medications",
            medicalVisits, v -> v.getId() + "," +
                v.getDate() + "," +
                (v.getChild() != null ? v.getChild().getId() : "") + "," +
                (v.getDoctor() != null ? v.getDoctor().getId() : "") + "," +
                v.getDescription() + "," +
                v.getRecommendations() + "," +
                v.getMedications());
    }

    @GetMapping("/menus/csv")
    public ResponseEntity<String> exportMenusToCsv() {
        List<Menu> menus = menuRepository.findAll();
        return exportToCsv("menus.csv", "id,date,session_id,breakfast,lunch,dinner,notes",
            menus, m -> m.getId() + "," +
                m.getDate() + "," +
                (m.getSession() != null ? m.getSession().getId() : "") + "," +
                m.getBreakfast() + "," +
                m.getLunch() + "," +
                m.getDinner() + "," +
                m.getNotes());
    }

    @GetMapping("/notifications/csv")
    public ResponseEntity<String> exportNotificationsToCsv() {
        List<Notification> notifications = notificationRepository.findAll();
        return exportToCsv("notifications.csv", "id,recipient_id,type,subject,message,status,created_at,sent_at",
            notifications, n -> n.getId() + "," +
                (n.getRecipient() != null ? n.getRecipient().getId() : "") + "," +
                n.getType() + "," +
                n.getSubject() + "," +
                n.getMessage() + "," +
                n.getStatus() + "," +
                n.getCreatedAt() + "," +
                n.getSentAt());
    }

    @GetMapping("/payments/csv")
    public ResponseEntity<String> exportPaymentsToCsv() {
        List<Payment> payments = paymentRepository.findAll();
        return exportToCsv("payments.csv", "id,parent_id,voucher_id,amount,date,status,method,comment",
            payments, p -> p.getId() + "," +
                (p.getParent() != null ? p.getParent().getId() : "") + "," +
                (p.getVoucher() != null ? p.getVoucher().getId() : "") + "," +
                p.getAmount() + "," +
                p.getDate() + "," +
                p.getStatus() + "," +
                p.getMethod() + "," +
                p.getComment());
    }

    @GetMapping("/schedules/csv")
    public ResponseEntity<String> exportSchedulesToCsv() {
        List<Schedule> schedules = scheduleRepository.findAll();
        return exportToCsv("schedules.csv", "id,session_id,employee_id,date,time,title,description,location,team",
            schedules, s -> s.getId() + "," +
                (s.getSession() != null ? s.getSession().getId() : "") + "," +
                (s.getEmployee() != null ? s.getEmployee().getId() : "") + "," +
                s.getDate() + "," +
                s.getTime() + "," +
                s.getTitle() + "," +
                s.getDescription() + "," +
                s.getLocation() + "," +
                s.getTeam());
    }

    @GetMapping("/users/csv")
    public ResponseEntity<String> exportUsersToCsv() {
        List<User> users = userRepository.findAll();
        return exportToCsv("users.csv", "id,username,email,role,email_notifications,sms_notifications,theme",
            users, u -> u.getId() + "," +
                u.getUsername() + "," +
                u.getEmail() + "," +
                u.getRole() + "," +
                u.isEmailNotifications() + "," +
                u.isSmsNotifications() + "," +
                u.getTheme());
    }

    @GetMapping("/vouchers/csv")
    public ResponseEntity<String> exportVouchersToCsv() {
        List<Voucher> vouchers = voucherRepository.findAll();
        return exportToCsv("vouchers.csv", "id,child_id,session_id,status,booking_date",
            vouchers, v -> v.getId() + "," +
                (v.getChild() != null ? v.getChild().getId() : "") + "," +
                (v.getSession() != null ? v.getSession().getId() : "") + "," +
                v.getStatus() + "," +
                v.getBookingDate());
    }
}
