package com.moneysaving.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reverse_spend")
public class ReverseSpend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 50)
    private String category;

    @Column(name = "budget_limit", precision = 10, scale = 2)
    private BigDecimal budgetLimit;

    @Column(name = "time_period", length = 20)
    private String timePeriod;

    @Column(name = "reward_percentage", precision = 5, scale = 2)
    private BigDecimal rewardPercentage;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(length = 20) // 'active', 'success', 'failed'
    private String status;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "current_spent", precision = 10, scale = 2)
    private BigDecimal currentSpent;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}