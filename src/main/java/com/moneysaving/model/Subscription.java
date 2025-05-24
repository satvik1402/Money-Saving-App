package com.moneysaving.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 100)
    private String name;
    
    @Column(name = "amount")
    private BigDecimal amount;
    
    @Column(name = "billing_cycle", length = 20) 
    private String billingCycle;
    
    @Column(length = 20)
    private String status; // 'Active', 'Inactive'
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(length = 50)
    private String category;
    
    @Column(name = "next_billing_date")
    private LocalDateTime nextBillingDate;
    
    @Column(name = "is_shared")
    private Boolean isShared;
    
    @Column(name = "shared_with")
    private String sharedWith;
    
    @Column(name = "last_used")
    private LocalDate lastUsed;
    
    @Column(name = "created_at")
    private LocalDate createdAt;
    
    @Column(name = "last_paid_date")
    private LocalDate lastPaidDate;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    @Transient
    private BigDecimal monthlyCost; // Derived from amount
    
    public BigDecimal getMonthlyCost() {
        if (monthlyCost != null) {
            return monthlyCost;
        }
        
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        
        // Convert to monthly cost based on billing cycle
        if ("Yearly".equalsIgnoreCase(billingCycle)) {
            return amount.divide(BigDecimal.valueOf(12), 2, BigDecimal.ROUND_HALF_UP);
        }
        
        return amount; // Default to the original amount
    }
}