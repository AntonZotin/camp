package ru.camp.server.service;

import org.springframework.stereotype.Service;
import ru.camp.server.model.ActivityLog;
import ru.camp.server.repository.ActivityLogRepository;
import java.util.List;

@Service
public class ActivityLogService {
    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public List<ActivityLog> getAll() {
        return activityLogRepository.findAll();
    }

    public ActivityLog getById(Long id) {
        return activityLogRepository.findById(id).orElse(null);
    }

    public ActivityLog create(ActivityLog log) {
        return activityLogRepository.save(log);
    }

    public ActivityLog update(Long id, ActivityLog log) {
        log.setId(id);
        return activityLogRepository.save(log);
    }

    public void delete(Long id) {
        activityLogRepository.deleteById(id);
    }
} 