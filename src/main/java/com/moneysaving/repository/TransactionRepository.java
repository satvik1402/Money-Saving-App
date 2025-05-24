package com.moneysaving.repository;

import com.moneysaving.model.Transaction;
import com.moneysaving.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByDateDesc(User user);
    
    List<Transaction> findByUserAndDateBetweenOrderByDateDesc(
        User user, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Transaction> findByUserAndCategoryOrderByDateDesc(User user, String category);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user = ?1 AND t.type = ?2 AND t.date BETWEEN ?3 AND ?4")
    BigDecimal sumAmountByUserAndTypeAndDateBetween(
        User user, String type, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t.category, SUM(t.amount) FROM Transaction t WHERE t.user = ?1 AND t.type = ?2 AND t.date BETWEEN ?3 AND ?4 GROUP BY t.category")
    List<Object[]> sumAmountByCategoryAndTypeAndDateBetween(
        User user, String type, LocalDateTime startDate, LocalDateTime endDate);
} 