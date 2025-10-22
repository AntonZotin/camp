package ru.camp.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.aspect.LogActivity;
import ru.camp.server.model.Menu;
import ru.camp.server.service.MenuService;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class MenuController {
    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    @LogActivity(action = "VIEW_MENUS", description = "Просмотр списка меню")
    public ResponseEntity<List<Menu>> getAll() {
        return ResponseEntity.ok(menuService.getAll());
    }

    @GetMapping("/{id}")
    @LogActivity(action = "VIEW_MENU", description = "Просмотр деталей меню")
    public ResponseEntity<Menu> getById(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getById(id));
    }

    @PostMapping
    @LogActivity(action = "CREATE_MENU", description = "Создание меню")
    public ResponseEntity<Menu> create(@RequestBody Menu menu) {
        return ResponseEntity.ok(menuService.create(menu));
    }

    @PutMapping("/{id}")
    @LogActivity(action = "UPDATE_MENU", description = "Обновление меню")
    public ResponseEntity<Menu> update(@PathVariable Long id, @RequestBody Menu menu) {
        return ResponseEntity.ok(menuService.update(id, menu));
    }

    @DeleteMapping("/{id}")
    @LogActivity(action = "DELETE_MENU", description = "Удаление меню")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 