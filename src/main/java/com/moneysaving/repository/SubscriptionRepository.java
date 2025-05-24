package com.moneysaving.repository;

import com.moneysaving.model.Subscription;
import com.moneysaving.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUser(User user);
    List<Subscription> findByUserAndStatus(User user, String status);
    
    List<Subscription> findByUserAndIsActiveTrue(User user);
    
    List<Subscription> findByUserAndIsActiveFalse(User user);
    
    List<Subscription> findByUserAndCategoryAndIsActiveTrue(User user, String category);
    
    List<Subscription> findByUserAndNextBillingDateBeforeAndIsActiveTrue(User user, LocalDateTime date);
    
    @Query("SELECT SUM(s.amount) FROM Subscription s WHERE s.user = ?1 AND s.isActive = true")
    BigDecimal sumActiveSubscriptionsByUser(User user);
    
    @Query("SELECT s.category, SUM(s.amount) FROM Subscription s WHERE s.user = ?1 AND s.isActive = true GROUP BY s.category")
    List<Object[]> sumAmountByCategoryAndUser(User user);
    
    List<Subscription> findByIsSharedTrueAndSharedWithContaining(String email);
} 