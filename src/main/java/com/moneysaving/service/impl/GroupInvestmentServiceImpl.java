package com.moneysaving.service.impl;

import com.moneysaving.model.GroupInvestment;
import com.moneysaving.model.User;
import com.moneysaving.repository.GroupInvestmentRepository;
import com.moneysaving.service.GroupInvestmentService;
import com.moneysaving.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class GroupInvestmentServiceImpl implements GroupInvestmentService {

    @Autowired
    private GroupInvestmentRepository groupInvestmentRepository;

    @Autowired
    private UserService userService;

    @Override
    public GroupInvestment createPot(GroupInvestment pot) {
        pot.setCreatedAt(LocalDateTime.now());
        pot.setTotalAmount(BigDecimal.ZERO);
        return groupInvestmentRepository.save(pot);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupInvestment> getUserPots(User user) {
        return groupInvestmentRepository.findByMembersContaining(user);
    }

    @Override
    public GroupInvestment updatePot(GroupInvestment pot) {
        if (!groupInvestmentRepository.existsById(pot.getId())) {
            throw new RuntimeException("Group investment pot not found");
        }
        return groupInvestmentRepository.save(pot);
    }

    @Override
    public void deletePot(Long id) {
        groupInvestmentRepository.deleteById(id);
    }

    @Override
    public void addContribution(Long potId, User user, BigDecimal amount) {
        GroupInvestment pot = groupInvestmentRepository.findById(potId)
                .orElseThrow(() -> new RuntimeException("Pot not found"));
        pot.setTotalAmount(pot.getTotalAmount().add(amount));
        // Optionally, update user's contribution record here
        groupInvestmentRepository.save(pot);
        // Optionally, update user's balance
        user.setTotalBalance(user.getTotalBalance().subtract(amount));
        userService.updateUser(user);
    }

    @Override
    public void withdrawContribution(Long potId, User user, BigDecimal amount) {
        GroupInvestment pot = groupInvestmentRepository.findById(potId)
                .orElseThrow(() -> new RuntimeException("Pot not found"));
        if (pot.getTotalAmount().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient pot balance");
        }
        pot.setTotalAmount(pot.getTotalAmount().subtract(amount));
        groupInvestmentRepository.save(pot);
        // Optionally, update user's balance
        user.setTotalBalance(user.getTotalBalance().add(amount));
        userService.updateUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPotAmount(Long potId) {
        GroupInvestment pot = groupInvestmentRepository.findById(potId)
                .orElseThrow(() -> new RuntimeException("Pot not found"));
        return pot.getTotalAmount();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getPotMembers(Long potId) {
        GroupInvestment pot = groupInvestmentRepository.findById(potId)
                .orElseThrow(() -> new RuntimeException("Pot not found"));
        return pot.getMembers();
    }
} 