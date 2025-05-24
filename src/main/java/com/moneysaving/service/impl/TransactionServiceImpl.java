package com.moneysaving.service.impl;

import com.moneysaving.model.Transaction;
import com.moneysaving.model.User;
import com.moneysaving.repository.TransactionRepository;
import com.moneysaving.service.TransactionService;
import com.moneysaving.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    @Override
    public Transaction createTransaction(Transaction transaction) {
        // Note: createdAt field no longer exists in Transaction model
        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDateTime.now());
        }
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        updateUserBalances(transaction.getUser());
        return savedTransaction;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactions(User user) {
        return transactionRepository.findByUserOrderByDateDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactionsByDateRange(User user, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByUserAndDateBetweenOrderByDateDesc(user, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactionsByCategory(User user, String category) {
        return transactionRepository.findByUserAndCategoryOrderByDateDesc(user, category);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalIncome(User user, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.sumAmountByUserAndTypeAndDateBetween(
            user, "credit", startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalExpenses(User user, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.sumAmountByUserAndTypeAndDateBetween(
            user, "debit", startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getCategoryWiseSpending(User user, LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = transactionRepository.sumAmountByCategoryAndTypeAndDateBetween(
            user, "debit", startDate, endDate);
        
        Map<String, BigDecimal> categorySpending = new HashMap<>();
        for (Object[] result : results) {
            categorySpending.put((String) result[0], (BigDecimal) result[1]);
        }
        return categorySpending;
    }

    @Override
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
        transactionRepository.deleteById(id);
        updateUserBalances(transaction.getUser());
    }

    @Override
    public Transaction updateTransaction(Transaction transaction) {
        if (!transactionRepository.existsById(transaction.getId())) {
            throw new RuntimeException("Transaction not found");
        }
        Transaction updatedTransaction = transactionRepository.save(transaction);
        updateUserBalances(transaction.getUser());
        return updatedTransaction;
    }

    private void updateUserBalances(User user) {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now().withDayOfMonth(1).plusMonths(1).minusSeconds(1);

        BigDecimal income = getTotalIncome(user, startOfMonth, endOfMonth);
        BigDecimal expenses = getTotalExpenses(user, startOfMonth, endOfMonth);
        
        user.setTotalBalance(income.subtract(expenses));
        userService.updateUser(user);
    }
} 