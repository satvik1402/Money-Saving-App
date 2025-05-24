package com.moneysaving.controller;

import com.moneysaving.model.GuiltSave;
import com.moneysaving.model.User;
import com.moneysaving.service.GuiltSaveService;
import com.moneysaving.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/guilt-saves")
public class GuiltSaveController {

    @Autowired
    private GuiltSaveService guiltSaveService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<GuiltSave> createGuiltSave(
            @RequestParam Long userId,
            @RequestParam String category,
            @RequestParam BigDecimal amount) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(guiltSaveService.createGuiltSave(user, category, amount));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GuiltSave>> getGuiltSavesByUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(guiltSaveService.getGuiltSavesByUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuiltSave> getGuiltSaveById(@PathVariable Long id) {
        return guiltSaveService.getGuiltSaveById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuiltSave> updateGuiltSave(
            @PathVariable Long id,
            @RequestBody GuiltSave guiltSave) {
        return guiltSaveService.getGuiltSaveById(id)
            .map(existing -> {
                guiltSave.setId(id);
                return ResponseEntity.ok(guiltSaveService.updateGuiltSave(guiltSave));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuiltSave(@PathVariable Long id) {
        return guiltSaveService.getGuiltSaveById(id)
            .map(guiltSave -> {
                guiltSaveService.deleteGuiltSave(id);
                return ResponseEntity.ok().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<GuiltSave> processGuiltSave(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        return guiltSaveService.getGuiltSaveById(id)
            .map(guiltSave -> {
                guiltSaveService.processGuiltSave(id, amount);
                return ResponseEntity.ok(guiltSave);
            })
            .orElse(ResponseEntity.notFound().build());
    }
} 