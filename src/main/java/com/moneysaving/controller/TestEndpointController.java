package com.moneysaving.controller;

import com.moneysaving.model.*;
import com.moneysaving.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * This controller provides endpoints that match the exact paths used in the test.html page
 */
@RestController
public class TestEndpointController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    /**
     * Get user by ID (directly at /users/{id})
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return userRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get transactions for a user (directly at /transactions/user/{userId})
     */
    @GetMapping("/transactions/user/{userId}")
    public ResponseEntity<?> getUserTransactions(@PathVariable Long userId) {
        return userRepository.findById(userId)
            .map(user -> ResponseEntity.ok(transactionRepository.findByUserOrderByDateDesc(user)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get category-wise spending for a user (directly at /transactions/user/{userId}/category-wise)
     */
    @GetMapping("/transactions/user/{userId}/category-wise")
    public ResponseEntity<?> getCategoryWiseSpending(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        List<Transaction> transactions = transactionRepository.findByUserOrderByDateDesc(user);
        
        // Group transactions by category
        Map<String, Double> categorySpending = new HashMap<>();
        for (Transaction transaction : transactions) {
            if ("debit".equalsIgnoreCase(transaction.getType())) {
                String category = transaction.getCategory();
                double amount = transaction.getAmount().doubleValue();
                categorySpending.put(category, categorySpending.getOrDefault(category, 0.0) + amount);
            }
        }
        
        return ResponseEntity.ok(categorySpending);
    }
    
    /**
     * Get subscriptions for a user (directly at /subscriptions/user/{userId})
     */
    @GetMapping("/subscriptions/user/{userId}")
    public ResponseEntity<?> getUserSubscriptions(@PathVariable Long userId) {
        return userRepository.findById(userId)
            .map(user -> {
                List<Subscription> subscriptions = subscriptionRepository.findByUser(user);
                return ResponseEntity.ok(subscriptions);
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
