package ru.camp.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.camp.server.model.Child;
import ru.camp.server.repository.ChildRepository;
import java.util.List;

@Service
public class ChildService {
    private final ChildRepository childRepository;

    @Autowired
    public ChildService(ChildRepository childRepository) {
        this.childRepository = childRepository;
    }

    public List<Child> getAll() {
        return childRepository.findAll();
    }

    public Child getById(Long id) {
        return childRepository.findById(id).orElse(null);
    }

    public List<Child> getByParentId(Long parentId) {
        return childRepository.findByParentId(parentId);
    }

    public Child create(Child child) {
        return childRepository.save(child);
    }

    public Child update(Long id, Child child) {
        child.setId(id);
        return childRepository.save(child);
    }

    public void delete(Long id) {
        childRepository.deleteById(id);
    }
} 