package com.moneysaving.service.impl;

import com.moneysaving.model.GuiltSave;
import com.moneysaving.model.Transaction;
import com.moneysaving.model.User;
import com.moneysaving.repository.GuiltSaveRepository;
import com.moneysaving.service.GuiltSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GuiltSaveServiceImpl implements GuiltSaveService {

    @Autowired
    private GuiltSaveRepository guiltSaveRepository;

    @Override
    public GuiltSave createGuiltSave(User user, String category, BigDecimal amount) {
        GuiltSave guiltSave = new GuiltSave();
        guiltSave.setUser(user);
        guiltSave.setCategory(category);
        guiltSave.setAmount(amount);
        guiltSave.setCreatedAt(LocalDateTime.now());
        guiltSave.setIsActive(true);
        guiltSave.setSavedAmount(BigDecimal.ZERO);
        return guiltSaveRepository.save(guiltSave);
    }

    @Override
    public List<GuiltSave> getGuiltSavesByUser(User user) {
        return guiltSaveRepository.findByUser(user);
    }

    @Override
    public Optional<GuiltSave> getGuiltSaveById(Long id) {
        return guiltSaveRepository.findById(id);
    }

    @Override
    public GuiltSave updateGuiltSave(GuiltSave guiltSave) {
        return guiltSaveRepository.save(guiltSave);
    }

    @Override
    public void deleteGuiltSave(Long id) {
        guiltSaveRepository.deleteById(id);
    }

    @Override
    public Transaction processGuiltSave(Long guiltSaveId, BigDecimal amount) {
        GuiltSave guiltSave = guiltSaveRepository.findById(guiltSaveId)
            .orElseThrow(() -> new RuntimeException("Guilt save not found"));

        Transaction savingsTransaction = new Transaction();
        savingsTransaction.setUser(guiltSave.getUser());
        savingsTransaction.setAmount(amount);
        savingsTransaction.setCategory("GUILT_SAVE");
        savingsTransaction.setDescription("Guilt save from " + guiltSave.getCategory());
        savingsTransaction.setType("SAVINGS");
        savingsTransaction.setTransactionDate(LocalDateTime.now());

        return savingsTransaction;
    }
} 