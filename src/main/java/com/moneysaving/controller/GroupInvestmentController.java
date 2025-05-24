package com.moneysaving.controller;

import com.moneysaving.model.GroupInvestment;
import com.moneysaving.model.User;
import com.moneysaving.service.GroupInvestmentService;
import com.moneysaving.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/group-investment")
public class GroupInvestmentController {

    @Autowired
    private GroupInvestmentService groupInvestmentService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<GroupInvestment> create(@RequestBody GroupInvestment pot) {
        // Hydrate members if present
        if (pot.getMembers() != null) {
            List<User> hydratedMembers = pot.getMembers().stream()
                .map(m -> userService.getUserById(m.getId()).orElseThrow(() -> new RuntimeException("User not found")))
                .toList();
            pot.setMembers(hydratedMembers);
        }
        return ResponseEntity.ok(groupInvestmentService.createPot(pot));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GroupInvestment>> getUserPots(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(groupInvestmentService.getUserPots(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupInvestment> update(@PathVariable Long id, @RequestBody GroupInvestment pot) {
        pot.setId(id);
        // Hydrate members if present
        if (pot.getMembers() != null) {
            List<User> hydratedMembers = pot.getMembers().stream()
                .map(m -> userService.getUserById(m.getId()).orElseThrow(() -> new RuntimeException("User not found")))
                .toList();
            pot.setMembers(hydratedMembers);
        }
        return ResponseEntity.ok(groupInvestmentService.updatePot(pot));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        groupInvestmentService.deletePot(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{potId}/contribute")
    public ResponseEntity<Void> addContribution(@PathVariable Long potId, @RequestParam Long userId, @RequestParam BigDecimal amount) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        groupInvestmentService.addContribution(potId, user, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{potId}/withdraw")
    public ResponseEntity<Void> withdrawContribution(@PathVariable Long potId, @RequestParam Long userId, @RequestParam BigDecimal amount) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        groupInvestmentService.withdrawContribution(potId, user, amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{potId}/total")
    public ResponseEntity<BigDecimal> getTotalPotAmount(@PathVariable Long potId) {
        return ResponseEntity.ok(groupInvestmentService.getTotalPotAmount(potId));
    }

    @GetMapping("/{potId}/members")
    public ResponseEntity<List<User>> getPotMembers(@PathVariable Long potId) {
        return ResponseEntity.ok(groupInvestmentService.getPotMembers(potId));
    }
} 