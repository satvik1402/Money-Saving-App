package com.moneysaving.controller;

import com.moneysaving.repository.UserRepository;
import com.moneysaving.repository.TransactionRepository;
import com.moneysaving.repository.AccountRepository;
import com.moneysaving.repository.SubscriptionRepository;
import com.moneysaving.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    @GetMapping("/db-check")
    public ResponseEntity<?> checkDatabase() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check user table
            long userCount = userRepository.count();
            Optional<User> firstUser = userRepository.findById(1L);
            
            // Check transaction table
            long transactionCount = transactionRepository.count();
            
            // Check account table
            long accountCount = accountRepository.count();
            
            // Check subscription table
            long subscriptionCount = subscriptionRepository.count();
            
            response.put("status", "connected");
            response.put("userCount", userCount);
            response.put("transactionCount", transactionCount);
            response.put("accountCount", accountCount);
            response.put("subscriptionCount", subscriptionCount);
            
            if (firstUser.isPresent()) {
                User user = firstUser.get();
                response.put("firstUserName", user.getName());
                response.put("firstUserEmail", user.getEmail());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            response.put("errorType", e.getClass().getName());
            return ResponseEntity.status(500).body(response);
        }
    }
}
