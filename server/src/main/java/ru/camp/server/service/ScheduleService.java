package ru.camp.server.service;

import org.springframework.stereotype.Service;
import ru.camp.server.model.Schedule;
import ru.camp.server.repository.ScheduleRepository;
import java.util.List;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<Schedule> getAll() {
        return scheduleRepository.findAll();
    }

    public Schedule getById(Long id) {
        return scheduleRepository.findById(id).orElse(null);
    }

    public Schedule create(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public Schedule update(Long id, Schedule schedule) {
        schedule.setId(id);
        return scheduleRepository.save(schedule);
    }

    public void delete(Long id) {
        scheduleRepository.deleteById(id);
    }

    public List<Schedule> getByEmployeeId(Long employeeId) {
        return scheduleRepository.findByEmployee_Id(employeeId);
    }
} 