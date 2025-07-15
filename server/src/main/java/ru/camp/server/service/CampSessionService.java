package ru.camp.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.camp.server.model.CampSession;
import ru.camp.server.repository.CampSessionRepository;
import java.util.List;

@Service
public class CampSessionService {
    private final CampSessionRepository campSessionRepository;

    @Autowired
    public CampSessionService(CampSessionRepository campSessionRepository) {
        this.campSessionRepository = campSessionRepository;
    }

    public List<CampSession> getAll() {
        return campSessionRepository.findAll();
    }

    public CampSession getById(Long id) {
        return campSessionRepository.findById(id).orElse(null);
    }

    public CampSession create(CampSession session) {
        return campSessionRepository.save(session);
    }

    public CampSession update(Long id, CampSession session) {
        session.setId(id);
        return campSessionRepository.save(session);
    }

    public void delete(Long id) {
        campSessionRepository.deleteById(id);
    }
} 