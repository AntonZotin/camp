package ru.camp.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.camp.server.model.DutyLog;
import ru.camp.server.repository.DutyLogRepository;
import java.util.List;

@Service
public class DutyLogService {
    private final DutyLogRepository dutyLogRepository;

    @Autowired
    public DutyLogService(DutyLogRepository dutyLogRepository) {
        this.dutyLogRepository = dutyLogRepository;
    }

    public List<DutyLog> getAll() {
        return dutyLogRepository.findAll();
    }

    public DutyLog getById(Long id) {
        return dutyLogRepository.findById(id).orElse(null);
    }

    public DutyLog create(DutyLog log) {
        return dutyLogRepository.save(log);
    }

    public DutyLog update(Long id, DutyLog log) {
        log.setId(id);
        return dutyLogRepository.save(log);
    }

    public void delete(Long id) {
        dutyLogRepository.deleteById(id);
    }

    public List<DutyLog> getByEmployeeId(Long employeeId) {
        return dutyLogRepository.findByEmployeeId(employeeId);
    }
} 