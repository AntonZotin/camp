package ru.camp.server.service;

import org.springframework.stereotype.Service;
import ru.camp.server.model.Menu;
import ru.camp.server.repository.MenuRepository;
import java.util.List;

@Service
public class MenuService {
    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<Menu> getAll() {
        return menuRepository.findAll();
    }

    public Menu getById(Long id) {
        return menuRepository.findById(id).orElse(null);
    }

    public Menu create(Menu menu) {
        return menuRepository.save(menu);
    }

    public Menu update(Long id, Menu menu) {
        menu.setId(id);
        return menuRepository.save(menu);
    }

    public void delete(Long id) {
        menuRepository.deleteById(id);
    }
} 