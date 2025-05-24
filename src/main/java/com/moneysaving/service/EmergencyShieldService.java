package com.moneysaving.service;

import com.moneysaving.model.EmergencyShield;
import com.moneysaving.model.User;
import java.math.BigDecimal;
import java.util.List;

public interface EmergencyShieldService {
    EmergencyShield createShield(User user, String friendName, String friendEmail, String friendPhone, 
                               String accountName, BigDecimal threshold);
    
    EmergencyShield getShield(Long id);
    
    List<EmergencyShield> getUserShields(User user);
    
    EmergencyShield updateShield(Long id, String friendName, String friendEmail, String friendPhone, 
                               String accountName, BigDecimal threshold);
    
    void deleteShield(Long id);
    
    void activateShield(Long id);
    
    void deactivateShield(Long id);
    
    BigDecimal getShieldedAmount(Long id);
} 