package com.moneysaving.controller;

import com.moneysaving.model.Transaction;
import com.moneysaving.model.User;
import com.moneysaving.service.TransactionService;
import com.moneysaving.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Transaction> create(@RequestBody Transaction transaction) {
        if (transaction.getUser() != null && transaction.getUser().getId() != null) {
            User user = userService.getUserById(transaction.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            transaction.setUser(user);
        }
        return ResponseEntity.ok(transactionService.createTransaction(transaction));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getUserTransactions(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(transactionService.getUserTransactions(user));
    }

    @GetMapping("/user/{userId}/range")
    public ResponseEntity<List<Transaction>> getUserTransactionsByDateRange(
            @PathVariable Long userId,
            @RequestParam String start,
            @RequestParam String end) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime startDate = LocalDate.parse(start).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(end).atTime(23, 59, 59);
        return ResponseEntity.ok(transactionService.getUserTransactionsByDateRange(user, startDate, endDate));
    }

    @GetMapping("/user/{userId}/category/{category}")
    public ResponseEntity<List<Transaction>> getUserTransactionsByCategory(
            @PathVariable Long userId,
            @PathVariable String category) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(transactionService.getUserTransactionsByCategory(user, category));
    }

    @GetMapping("/user/{userId}/income")
    public ResponseEntity<BigDecimal> getTotalIncome(@PathVariable Long userId,
                                                     @RequestParam String start,
                                                     @RequestParam String end) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime startDate = LocalDate.parse(start).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(end).atTime(23, 59, 59);
        return ResponseEntity.ok(transactionService.getTotalIncome(user, startDate, endDate));
    }

    @GetMapping("/user/{userId}/expenses")
    public ResponseEntity<BigDecimal> getTotalExpenses(@PathVariable Long userId,
                                                       @RequestParam String start,
                                                       @RequestParam String end) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime startDate = LocalDate.parse(start).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(end).atTime(23, 59, 59);
        return ResponseEntity.ok(transactionService.getTotalExpenses(user, startDate, endDate));
    }

    @GetMapping("/user/{userId}/category-wise")
    public ResponseEntity<Map<String, BigDecimal>> getCategoryWiseSpending(@PathVariable Long userId,
                                                                           @RequestParam String start,
                                                                           @RequestParam String end) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime startDate = LocalDate.parse(start).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(end).atTime(23, 59, 59);
        return ResponseEntity.ok(transactionService.getCategoryWiseSpending(user, startDate, endDate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> update(@PathVariable Long id, @RequestBody Transaction transaction) {
        transaction.setId(id);
        if (transaction.getUser() != null && transaction.getUser().getId() != null) {
            User user = userService.getUserById(transaction.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            transaction.setUser(user);
        }
        return ResponseEntity.ok(transactionService.updateTransaction(transaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
} 