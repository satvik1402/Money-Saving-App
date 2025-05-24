package com.moneysaving.service;

import com.moneysaving.model.GuiltSave;
import com.moneysaving.model.User;
import com.moneysaving.model.Transaction;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface GuiltSaveService {
    GuiltSave createGuiltSave(User user, String category, BigDecimal amount);
    List<GuiltSave> getGuiltSavesByUser(User user);
    Optional<GuiltSave> getGuiltSaveById(Long id);
    GuiltSave updateGuiltSave(GuiltSave guiltSave);
    void deleteGuiltSave(Long id);
    Transaction processGuiltSave(Long guiltSaveId, BigDecimal amount);
} 