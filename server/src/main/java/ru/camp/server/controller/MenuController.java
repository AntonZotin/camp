package ru.camp.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.camp.server.model.Menu;
import ru.camp.server.service.MenuService;
import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class MenuController {
    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public ResponseEntity<List<Menu>> getAll() {
        return ResponseEntity.ok(menuService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Menu> getById(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Menu> create(@RequestBody Menu menu) {
        return ResponseEntity.ok(menuService.create(menu));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Menu> update(@PathVariable Long id, @RequestBody Menu menu) {
        return ResponseEntity.ok(menuService.update(id, menu));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 