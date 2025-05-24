package com.moneysaving.service;

import com.moneysaving.model.ReverseSpend;
import com.moneysaving.model.User;
import java.time.LocalDateTime;
import java.util.List;

public interface ReverseSpendService {
    ReverseSpend createChallenge(ReverseSpend challenge);
    List<ReverseSpend> getActiveChallenges(User user);
    List<ReverseSpend> getCompletedChallenges(User user);
    ReverseSpend updateChallenge(ReverseSpend challenge);
    void deleteChallenge(Long id);
    void checkAndUpdateChallenges(User user);
    List<ReverseSpend> getExpiredChallenges(User user);
    List<ReverseSpend> getChallengesByCategory(User user, String category);
} 