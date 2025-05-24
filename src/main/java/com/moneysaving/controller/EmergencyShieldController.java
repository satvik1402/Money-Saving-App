package com.moneysaving.controller;

import com.moneysaving.model.EmergencyShield;
import com.moneysaving.model.User;
import com.moneysaving.service.EmergencyShieldService;
import com.moneysaving.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/emergency-shield")
public class EmergencyShieldController {

    @Autowired
    private EmergencyShieldService emergencyShieldService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<EmergencyShield> create(@RequestBody EmergencyShield shield) {
        if (shield.getUser() == null || shield.getUser().getId() == null) {
            throw new RuntimeException("User is required");
        }
        User user = userService.getUserById(shield.getUser().getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        EmergencyShield created = emergencyShieldService.createShield(
            user,
            shield.getFriendName(),
            shield.getFriendEmail(),
            shield.getFriendPhone(),
            shield.getAccountName(),
            shield.getThreshold()
        );
        return ResponseEntity.ok(created);
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<EmergencyShield>> getActive(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(emergencyShieldService.getUserShields(user));
    }

    @GetMapping("/user/{userId}/inactive")
    public ResponseEntity<List<EmergencyShield>> getInactive(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        List<EmergencyShield> allShields = emergencyShieldService.getUserShields(user);
        List<EmergencyShield> inactive = allShields.stream().filter(s -> Boolean.FALSE.equals(s.getIsActive())).toList();
        return ResponseEntity.ok(inactive);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmergencyShield> update(@PathVariable Long id, @RequestBody EmergencyShield shield) {
        shield.setId(id);
        if (shield.getUser() == null || shield.getUser().getId() == null) {
            throw new RuntimeException("User is required");
        }
        User user = userService.getUserById(shield.getUser().getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        EmergencyShield updated = emergencyShieldService.updateShield(
            id,
            shield.getFriendName(),
            shield.getFriendEmail(),
            shield.getFriendPhone(),
            shield.getAccountName(),
            shield.getThreshold()
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        emergencyShieldService.deleteShield(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/total-shielded")
    public ResponseEntity<BigDecimal> getTotalShielded(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        // Assuming getShieldedAmount should sum all shields for the user
        List<EmergencyShield> shields = emergencyShieldService.getUserShields(user);
        BigDecimal total = shields.stream().map(EmergencyShield::getShieldedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(total);
    }
} 