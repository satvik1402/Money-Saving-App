package com.moneysaving.service;

import com.moneysaving.model.GroupInvestment;
import com.moneysaving.model.User;
import java.math.BigDecimal;
import java.util.List;

public interface GroupInvestmentService {
    GroupInvestment createPot(GroupInvestment pot);
    List<GroupInvestment> getUserPots(User user);
    GroupInvestment updatePot(GroupInvestment pot);
    void deletePot(Long id);
    void addContribution(Long potId, User user, BigDecimal amount);
    void withdrawContribution(Long potId, User user, BigDecimal amount);
    BigDecimal getTotalPotAmount(Long potId);
    List<User> getPotMembers(Long potId);
} 