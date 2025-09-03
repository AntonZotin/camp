package ru.camp.server.service;

import org.springframework.stereotype.Service;
import ru.camp.server.model.DutyLog;
import ru.camp.server.model.DutyStatus;
import ru.camp.server.model.Schedule;
import ru.camp.server.repository.DutyLogRepository;
import ru.camp.server.repository.ScheduleRepository;

import java.time.LocalTime;
import java.util.List;

@Service
public class DutyLogService {
    private final DutyLogRepository dutyLogRepository;
    private final ScheduleRepository scheduleRepository;

    public DutyLogService(DutyLogRepository dutyLogRepository, ScheduleRepository scheduleRepository) {
        this.dutyLogRepository = dutyLogRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public List<DutyLog> getAll() {
        return dutyLogRepository.findAll();
    }

    public DutyLog getById(Long id) {
        return dutyLogRepository.findById(id).orElse(null);
    }

    public List<DutyLog> getByEmployeeId(Long employeeId) {
        return dutyLogRepository.findBySchedule_Employee_Id(employeeId);
    }

    public List<DutyLog> getByScheduleId(Long scheduleId) {
        return dutyLogRepository.findBySchedule_Id(scheduleId);
    }

    public DutyLog startFromSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new RuntimeException("Schedule not found"));

        DutyLog dutyLog = new DutyLog();
        dutyLog.setSchedule(schedule);
        dutyLog.setDate(schedule.getDate());
        dutyLog.setStartTime(schedule.getTime());
        LocalTime endTime = schedule.getTime().plusHours(2);
        dutyLog.setEndTime(endTime);

        dutyLog.setStatus(DutyStatus.PLANNED);
        dutyLog.setNotes("");
        dutyLog.setReport("");

        return dutyLogRepository.save(dutyLog);
    }

    public DutyLog create(DutyLog dutyLog) {
        return dutyLogRepository.save(dutyLog);
    }

    public DutyLog update(Long id, DutyLog dutyLogDetails) {
        DutyLog dutyLog = getById(id);

        if (dutyLogDetails.getStatus() != null) {
            dutyLog.setStatus(dutyLogDetails.getStatus());
        }
        if (dutyLogDetails.getNotes() != null) {
            dutyLog.setNotes(dutyLogDetails.getNotes());
        }
        if (dutyLogDetails.getReport() != null) {
            dutyLog.setReport(dutyLogDetails.getReport());
        }
        if (dutyLogDetails.getEndTime() != null) {
            dutyLog.setEndTime(dutyLogDetails.getEndTime());
        }

        return dutyLogRepository.save(dutyLog);
    }

    public void delete(Long id) {
        dutyLogRepository.deleteById(id);
    }
}