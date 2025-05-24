package com.moneysaving.service;

import com.moneysaving.model.Transaction;
import com.moneysaving.model.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction);
    List<Transaction> getUserTransactions(User user);
    List<Transaction> getUserTransactionsByDateRange(User user, LocalDateTime startDate, LocalDateTime endDate);
    List<Transaction> getUserTransactionsByCategory(User user, String category);
    BigDecimal getTotalIncome(User user, LocalDateTime startDate, LocalDateTime endDate);
    BigDecimal getTotalExpenses(User user, LocalDateTime startDate, LocalDateTime endDate);
    Map<String, BigDecimal> getCategoryWiseSpending(User user, LocalDateTime startDate, LocalDateTime endDate);
    void deleteTransaction(Long id);
    Transaction updateTransaction(Transaction transaction);
} 