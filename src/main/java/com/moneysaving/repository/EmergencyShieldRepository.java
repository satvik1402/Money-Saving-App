package com.moneysaving.repository;

import com.moneysaving.model.EmergencyShield;
import com.moneysaving.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmergencyShieldRepository extends JpaRepository<EmergencyShield, Long> {
    List<EmergencyShield> findByUser(User user);
    
    List<EmergencyShield> findByUserAndIsActiveFalse(User user);
    
    Optional<EmergencyShield> findByUserAndAccountNameAndIsActiveTrue(User user, String accountName);
    
    List<EmergencyShield> findByFriendEmailAndIsActiveTrue(String email);
} 