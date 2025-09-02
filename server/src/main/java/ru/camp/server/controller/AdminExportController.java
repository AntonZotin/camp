package ru.camp.server.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.camp.server.model.*;
import ru.camp.server.repository.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

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

    private <T> ResponseEntity<byte[]> exportToXlsx(String entityName, String[] headers, List<T> items, Function<T, String[]> mapFunction) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(entityName);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (T item : items) {
                String[] rowValues = mapFunction.apply(item);
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < rowValues.length; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(rowValues[i]);
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + entityName + ".xlsx")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/activity-logs/xlsx")
    public ResponseEntity<byte[]> exportActivityLogsToXlsx() {
        List<ActivityLog> activityLogs = activityLogRepository.findAll();
        return exportToXlsx("activity-logs", new String[] {"id,user_id,action,timestamp,description"},
            activityLogs, a -> new String[] {
                a.getId() + "," +
                    (a.getUser() != null ? a.getUser().getId() : "") + "," +
                    a.getAction() + "," +
                    a.getTimestamp() + "," +
                    a.getDescription()
            });
    }

    @GetMapping("/camp-sessions/xlsx")
    public ResponseEntity<byte[]> exportCampSessionsToXlsx() {
        List<CampSession> campSessions = campSessionRepository.findAll();
        return exportToXlsx("camp-sessions", new String[] {"id,name,start_date,end_date,description,max_children,price"},
            campSessions, c -> new String[] {
                c.getId() + "," +
                    c.getName() + "," +
                    c.getStartDate() + "," +
                    c.getEndDate() + "," +
                    c.getDescription() + "," +
                    c.getMaxChildren() + "," +
                    c.getPrice()
            });
    }

    @GetMapping("/children/xlsx")
    public ResponseEntity<byte[]> exportChildrenToXlsx() {
        List<Child> children = childRepository.findAll();
        return exportToXlsx("children", new String[] {"id,full_name,birth_date,parent_id,parent_username"},
            children, c -> new String[] {
                c.getId() + "," +
                    c.getFullName() + "," +
                    c.getBirthDate() + "," +
                    (c.getParent() != null ? c.getParent().getId() : "") + "," +
                    (c.getParent() != null ? c.getParent().getUsername() : "")
            });
    }

    @GetMapping("/duty-logs/xlsx")
    public ResponseEntity<byte[]> exportDutyLogsToXlsx() {
        List<DutyLog> dutyLogs = dutyLogRepository.findAll();
        return exportToXlsx("duty-logs", new String[] {"id,date,employee_id,session_id,start_time,end_time,location,status,description,notes,report"},
            dutyLogs, d -> new String[] {
                d.getId() + "," +
                    d.getDate() + "," +
                    (d.getEmployee() != null ? d.getEmployee().getId() : "") + "," +
                    (d.getSession() != null ? d.getSession().getId() : "") + "," +
                    d.getStartTime() + "," +
                    d.getEndTime() + "," +
                    d.getLocation() + "," +
                    d.getStatus() + "," +
                    d.getDescription() + "," +
                    d.getNotes() + "," +
                    d.getReport()
            });
    }

    @GetMapping("/employees/xlsx")
    public ResponseEntity<byte[]> exportEmployeesToXlsx() {
        List<Employee> employees = employeeRepository.findAll();
        return exportToXlsx("employees", new String[] {"id,full_name,position,user_id,username"},
            employees, e -> new String[] {
                e.getId() + "," +
                    e.getFullName() + "," +
                    e.getPosition() + "," +
                    (e.getUser() != null ? e.getUser().getId() : "") + "," +
                    (e.getUser() != null ? e.getUser().getUsername() : "")
            });
    }

    @GetMapping("/medical-cards/xlsx")
    public ResponseEntity<byte[]> exportMedicalCardsToXlsx() {
        List<MedicalCard> medicalCards = medicalCardRepository.findAll();
        return exportToXlsx("medical-cards", new String[] {"id,child_id,health_info,chronic_diseases,allergies,vaccinations,notes"},
            medicalCards, m -> new String[] {
                m.getId() + "," +
                    (m.getChild() != null ? m.getChild().getId() : "") + "," +
                    m.getHealthInfo() + "," +
                    m.getChronicDiseases() + "," +
                    m.getAllergies() + "," +
                    m.getVaccinations() + "," +
                    m.getNotes()
            });
    }

    @GetMapping("/medical-visits/xlsx")
    public ResponseEntity<byte[]> exportMedicalVisitsToXlsx() {
        List<MedicalVisit> medicalVisits = medicalVisitRepository.findAll();
        return exportToXlsx("medical-visits", new String[] {"id,date,child_id,doctor_id,description,recommendations,medications"},
            medicalVisits, v -> new String[] {
                v.getId() + "," +
                    v.getDate() + "," +
                    (v.getChild() != null ? v.getChild().getId() : "") + "," +
                    (v.getDoctor() != null ? v.getDoctor().getId() : "") + "," +
                    v.getDescription() + "," +
                    v.getRecommendations() + "," +
                    v.getMedications()
            });
    }

    @GetMapping("/menus/xlsx")
    public ResponseEntity<byte[]> exportMenusToXlsx() {
        List<Menu> menus = menuRepository.findAll();
        return exportToXlsx("menus", new String[] {"id,date,session_id,breakfast,lunch,dinner,notes"},
            menus, m -> new String[] {
                m.getId() + "," +
                    m.getDate() + "," +
                    (m.getSession() != null ? m.getSession().getId() : "") + "," +
                    m.getBreakfast() + "," +
                    m.getLunch() + "," +
                    m.getDinner() + "," +
                    m.getNotes()
            });
    }

    @GetMapping("/notifications/xlsx")
    public ResponseEntity<byte[]> exportNotificationsToXlsx() {
        List<Notification> notifications = notificationRepository.findAll();
        return exportToXlsx("notifications", new String[] {"id,recipient_id,type,subject,message,status,created_at,sent_at"},
            notifications, n -> new String[] {
                n.getId() + "," +
                    (n.getRecipient() != null ? n.getRecipient().getId() : "") + "," +
                    n.getType() + "," +
                    n.getSubject() + "," +
                    n.getMessage() + "," +
                    n.getStatus() + "," +
                    n.getCreatedAt() + "," +
                    n.getSentAt()
            });
    }

    @GetMapping("/payments/xlsx")
    public ResponseEntity<byte[]> exportPaymentsToXlsx() {
        List<Payment> payments = paymentRepository.findAll();
        return exportToXlsx("payments", new String[] {"id,parent_id,voucher_id,amount,date,status,method,comment"},
            payments, p -> new String[] {
                p.getId() + "," +
                    (p.getParent() != null ? p.getParent().getId() : "") + "," +
                    (p.getVoucher() != null ? p.getVoucher().getId() : "") + "," +
                    p.getAmount() + "," +
                    p.getDate() + "," +
                    p.getStatus() + "," +
                    p.getMethod() + "," +
                    p.getComment()
            });
    }

    @GetMapping("/schedules/xlsx")
    public ResponseEntity<byte[]> exportSchedulesToXlsx() {
        List<Schedule> schedules = scheduleRepository.findAll();
        return exportToXlsx("schedules", new String[] {"id,session_id,employee_id,date,time,title,description,location,team"},
            schedules, s -> new String[] {
                s.getId() + "," +
                    (s.getSession() != null ? s.getSession().getId() : "") + "," +
                    (s.getEmployee() != null ? s.getEmployee().getId() : "") + "," +
                    s.getDate() + "," +
                    s.getTime() + "," +
                    s.getTitle() + "," +
                    s.getDescription() + "," +
                    s.getLocation() + "," +
                    s.getTeam()
            });
    }

    @GetMapping("/users/xlsx")
    public ResponseEntity<byte[]> exportUsersToXlsx() {
        List<User> users = userRepository.findAll();
        return exportToXlsx("users", new String[] {"id,username,email,role,email_notifications,sms_notifications,theme"},
            users, u -> new String[] {
                u.getId() + "," +
                    u.getUsername() + "," +
                    u.getEmail() + "," +
                    u.getRole() + "," +
                    u.isEmailNotifications() + "," +
                    u.isSmsNotifications() + "," +
                    u.getTheme()
            });
    }

    @GetMapping("/vouchers/xlsx")
    public ResponseEntity<byte[]> exportVouchersToXlsx() {
        List<Voucher> vouchers = voucherRepository.findAll();
        return exportToXlsx("vouchers", new String[] {"id,child_id,session_id,status,booking_date"},
            vouchers, v -> new String[] {
                v.getId() + "," +
                    (v.getChild() != null ? v.getChild().getId() : "") + "," +
                    (v.getSession() != null ? v.getSession().getId() : "") + "," +
                    v.getStatus() + "," +
                    v.getBookingDate()
            });
    }
}
