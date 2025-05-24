package com.moneysaving.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "emergency_shield")
public class EmergencyShield {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "friend_name", length = 100)
    private String friendName;

    @Column(name = "friend_email", length = 150)
    private String friendEmail;

    @Column(name = "friend_phone", length = 20)
    private String friendPhone;

    @Column(name = "account_name", length = 50)
    private String accountName;

    @Column(precision = 10, scale = 2)
    private BigDecimal threshold;

    @Column(name = "active")
    private Boolean isActive;

    @Column(name = "shielded_amount", precision = 10, scale = 2)
    private BigDecimal shieldedAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 