package com.moneysaving.service.impl;

import com.moneysaving.model.Subscription;
import com.moneysaving.model.User;
import com.moneysaving.repository.SubscriptionRepository;
import com.moneysaving.service.SubscriptionService;
import com.moneysaving.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserService userService;

    @Override
    public Subscription createSubscription(Subscription subscription) {
        subscription.setCreatedAt(LocalDate.now());
        return subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subscription> getUserSubscriptions(User user) {
        return subscriptionRepository.findByUser(user);
    }

    @Override
    public Subscription updateSubscription(Subscription subscription) {
        if (!subscriptionRepository.existsById(subscription.getId())) {
            throw new RuntimeException("Subscription not found");
        }
        return subscriptionRepository.save(subscription);
    }

    @Override
    public void deleteSubscription(Long id) {
        subscriptionRepository.deleteById(id);
    }

    @Override
    public void processSubscriptionPayment(Long subscriptionId, BigDecimal amount) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
        User user = subscription.getUser();
        // Deduct payment from user's balance
        user.setTotalBalance(user.getTotalBalance().subtract(amount));
        userService.updateUser(user);
        // Optionally, update subscription's last payment date
        subscription.setLastPaidDate(LocalDate.now());
        subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalMonthlySubscriptionCost(User user) {
        List<Subscription> subscriptions = getUserSubscriptions(user);
        return subscriptions.stream()
                .map(Subscription::getMonthlyCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subscription> getExpiringSubscriptions(User user) {
        LocalDate now = LocalDate.now();
        return getUserSubscriptions(user).stream()
                .filter(sub -> sub.getExpiryDate() != null && sub.getExpiryDate().isBefore(now.plusDays(7)))
                .collect(Collectors.toList());
    }
} 