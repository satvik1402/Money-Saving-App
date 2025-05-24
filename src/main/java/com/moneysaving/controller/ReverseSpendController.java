package com.moneysaving.controller;

import com.moneysaving.model.ReverseSpend;
import com.moneysaving.model.User;
import com.moneysaving.service.ReverseSpendService;
import com.moneysaving.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reverse-spend")
public class ReverseSpendController {

    @Autowired
    private ReverseSpendService reverseSpendService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<ReverseSpend> create(@RequestBody ReverseSpend challenge) {
        if (challenge.getUser() != null && challenge.getUser().getId() != null) {
            User user = userService.getUserById(challenge.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            challenge.setUser(user);
        }
        return ResponseEntity.ok(reverseSpendService.createChallenge(challenge));
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<ReverseSpend>> getActive(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(reverseSpendService.getActiveChallenges(user));
    }

    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<List<ReverseSpend>> getCompleted(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(reverseSpendService.getCompletedChallenges(user));
    }

    @GetMapping("/user/{userId}/expired")
    public ResponseEntity<List<ReverseSpend>> getExpired(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(reverseSpendService.getExpiredChallenges(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReverseSpend> update(@PathVariable Long id, @RequestBody ReverseSpend challenge) {
        challenge.setId(id);
        if (challenge.getUser() != null && challenge.getUser().getId() != null) {
            User user = userService.getUserById(challenge.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            challenge.setUser(user);
        }
        return ResponseEntity.ok(reverseSpendService.updateChallenge(challenge));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reverseSpendService.deleteChallenge(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/user/{userId}/check-update")
    public ResponseEntity<Void> checkAndUpdate(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        reverseSpendService.checkAndUpdateChallenges(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/category/{category}")
    public ResponseEntity<List<ReverseSpend>> getByCategory(@PathVariable Long userId, @PathVariable String category) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(reverseSpendService.getChallengesByCategory(user, category));
    }
} 