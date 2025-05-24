package com.moneysaving.service;

import com.moneysaving.model.Subscription;
import com.moneysaving.model.User;
import java.math.BigDecimal;
import java.util.List;

public interface SubscriptionService {
    Subscription createSubscription(Subscription subscription);
    List<Subscription> getUserSubscriptions(User user);
    Subscription updateSubscription(Subscription subscription);
    void deleteSubscription(Long id);
    void processSubscriptionPayment(Long subscriptionId, BigDecimal amount);
    BigDecimal getTotalMonthlySubscriptionCost(User user);
    List<Subscription> getExpiringSubscriptions(User user);
} 