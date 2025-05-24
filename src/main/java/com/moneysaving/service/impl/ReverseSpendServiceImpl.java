package com.moneysaving.service.impl;

import com.moneysaving.model.ReverseSpend;
import com.moneysaving.model.User;
import com.moneysaving.repository.ReverseSpendRepository;
import com.moneysaving.service.ReverseSpendService;
import com.moneysaving.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReverseSpendServiceImpl implements ReverseSpendService {

    @Autowired
    private ReverseSpendRepository reverseSpendRepository;

    @Autowired
    private TransactionService transactionService;

    @Override
    public ReverseSpend createChallenge(ReverseSpend challenge) {
        challenge.setCreatedAt(LocalDateTime.now());
        challenge.setIsActive(true);
        return reverseSpendRepository.save(challenge);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReverseSpend> getActiveChallenges(User user) {
        return reverseSpendRepository.findByUserAndIsActiveTrue(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReverseSpend> getCompletedChallenges(User user) {
        return reverseSpendRepository.findByUserAndIsActiveFalse(user);
    }

    @Override
    public ReverseSpend updateChallenge(ReverseSpend challenge) {
        if (!reverseSpendRepository.existsById(challenge.getId())) {
            throw new RuntimeException("Challenge not found");
        }
        return reverseSpendRepository.save(challenge);
    }

    @Override
    public void deleteChallenge(Long id) {
        reverseSpendRepository.deleteById(id);
    }

    @Override
    public void checkAndUpdateChallenges(User user) {
        List<ReverseSpend> activeChallenges = getActiveChallenges(user);
        LocalDateTime now = LocalDateTime.now();

        for (ReverseSpend challenge : activeChallenges) {
            // Check if challenge has expired
            if (now.isAfter(challenge.getEndDate())) {
                challenge.setIsActive(false);
                reverseSpendRepository.save(challenge);
                continue;
            }

            // Calculate current spending in the category
            BigDecimal currentSpent = calculateCategorySpending(user, challenge.getCategory(), 
                challenge.getStartDate(), now);

            // Update current spent amount
            challenge.setCurrentSpent(currentSpent);
            reverseSpendRepository.save(challenge);

            // Check if budget limit is exceeded
            if (currentSpent.compareTo(challenge.getBudgetLimit()) > 0) {
                challenge.setIsActive(false);
                reverseSpendRepository.save(challenge);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReverseSpend> getExpiredChallenges(User user) {
        return reverseSpendRepository.findByUserAndEndDateBeforeAndIsActiveTrue(user, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReverseSpend> getChallengesByCategory(User user, String category) {
        return reverseSpendRepository.findByUserAndCategoryAndIsActiveTrue(user, category);
    }

    private BigDecimal calculateCategorySpending(User user, String category, 
            LocalDateTime startDate, LocalDateTime endDate) {
        return transactionService.getTotalExpenses(user, startDate, endDate);
    }
} 