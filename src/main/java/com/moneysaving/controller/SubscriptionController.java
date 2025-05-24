package com.moneysaving.controller;

import com.moneysaving.model.Subscription;
import com.moneysaving.model.User;
import com.moneysaving.service.SubscriptionService;
import com.moneysaving.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Subscription> create(@RequestBody Subscription subscription) {
        if (subscription.getUser() != null && subscription.getUser().getId() != null) {
            User user = userService.getUserById(subscription.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            subscription.setUser(user);
        }
        return ResponseEntity.ok(subscriptionService.createSubscription(subscription));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Subscription>> getUserSubscriptions(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subscription> update(@PathVariable Long id, @RequestBody Subscription subscription) {
        subscription.setId(id);
        if (subscription.getUser() != null && subscription.getUser().getId() != null) {
            User user = userService.getUserById(subscription.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            subscription.setUser(user);
        }
        return ResponseEntity.ok(subscriptionService.updateSubscription(subscription));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Void> processPayment(@PathVariable Long id, @RequestParam BigDecimal amount) {
        subscriptionService.processSubscriptionPayment(id, amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/total-monthly-cost")
    public ResponseEntity<BigDecimal> getTotalMonthlyCost(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(subscriptionService.getTotalMonthlySubscriptionCost(user));
    }

    @GetMapping("/user/{userId}/expiring")
    public ResponseEntity<List<Subscription>> getExpiring(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(subscriptionService.getExpiringSubscriptions(user));
    }
} 