package com.moneysaving.service.impl;

import com.moneysaving.model.EmergencyShield;
import com.moneysaving.model.User;
import com.moneysaving.repository.EmergencyShieldRepository;
import com.moneysaving.service.EmergencyShieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EmergencyShieldServiceImpl implements EmergencyShieldService {

    @Autowired
    private EmergencyShieldRepository emergencyShieldRepository;

    @Override
    public EmergencyShield createShield(User user, String friendName, String friendEmail, String friendPhone,
                                      String accountName, BigDecimal threshold) {
        EmergencyShield shield = new EmergencyShield();
        shield.setUser(user);
        shield.setFriendName(friendName);
        shield.setFriendEmail(friendEmail);
        shield.setFriendPhone(friendPhone);
        shield.setAccountName(accountName);
        shield.setThreshold(threshold);
        shield.setCreatedAt(LocalDateTime.now());
        shield.setIsActive(true);
        shield.setShieldedAmount(BigDecimal.ZERO);
        return emergencyShieldRepository.save(shield);
    }

    @Override
    public EmergencyShield getShield(Long id) {
        return emergencyShieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shield not found"));
    }

    @Override
    public List<EmergencyShield> getUserShields(User user) {
        return emergencyShieldRepository.findByUser(user);
    }

    @Override
    public EmergencyShield updateShield(Long id, String friendName, String friendEmail, String friendPhone,
                                      String accountName, BigDecimal threshold) {
        EmergencyShield shield = getShield(id);
        shield.setFriendName(friendName);
        shield.setFriendEmail(friendEmail);
        shield.setFriendPhone(friendPhone);
        shield.setAccountName(accountName);
        shield.setThreshold(threshold);
        return emergencyShieldRepository.save(shield);
    }

    @Override
    public void deleteShield(Long id) {
        emergencyShieldRepository.deleteById(id);
    }

    @Override
    public void activateShield(Long id) {
        EmergencyShield shield = getShield(id);
        shield.setIsActive(true);
        emergencyShieldRepository.save(shield);
    }

    @Override
    public void deactivateShield(Long id) {
        EmergencyShield shield = getShield(id);
        shield.setIsActive(false);
        emergencyShieldRepository.save(shield);
    }

    @Override
    public BigDecimal getShieldedAmount(Long id) {
        return getShield(id).getShieldedAmount();
    }
} 