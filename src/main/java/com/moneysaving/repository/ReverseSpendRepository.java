package com.moneysaving.repository;

import com.moneysaving.model.ReverseSpend;
import com.moneysaving.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReverseSpendRepository extends JpaRepository<ReverseSpend, Long> {
    List<ReverseSpend> findByUserAndIsActiveTrue(User user);
    
    List<ReverseSpend> findByUserAndIsActiveFalse(User user);
    
    List<ReverseSpend> findByUserAndEndDateBeforeAndIsActiveTrue(User user, LocalDateTime date);
    
    List<ReverseSpend> findByUserAndCategoryAndIsActiveTrue(User user, String category);
} 