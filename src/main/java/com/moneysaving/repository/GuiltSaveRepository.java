package com.moneysaving.repository;

import com.moneysaving.model.GuiltSave;
import com.moneysaving.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GuiltSaveRepository extends JpaRepository<GuiltSave, Long> {
    List<GuiltSave> findByUserAndIsActiveTrue(User user);
    
    List<GuiltSave> findByUserAndIsActiveFalse(User user);
    
    List<GuiltSave> findByUser(User user);
    
    List<GuiltSave> findByUserAndCategoryAndIsActiveTrue(User user, String category);
    
    List<GuiltSave> findByUserAndDestinationAccountAndIsActiveTrue(User user, String destinationAccount);
} 