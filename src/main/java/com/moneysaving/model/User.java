package com.moneysaving.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String name;
    
    @Column(length = 255, unique = true)
    private String email;
    
    @Column(name = "password_hash", length = 255)
    private String passwordHash;
    
    @Column(length = 255)
    private String password; // For login purposes

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "total_balance", precision = 19, scale = 2)
    private BigDecimal totalBalance;
    
    @Column(name = "savings_balance", precision = 19, scale = 2)
    private BigDecimal savingsBalance;
    
    @Column(name = "emergency_fund_balance", precision = 19, scale = 2)
    private BigDecimal emergencyFundBalance;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (totalBalance == null) {
            totalBalance = BigDecimal.ZERO;
        }
        if (savingsBalance == null) {
            savingsBalance = BigDecimal.ZERO;
        }
        if (emergencyFundBalance == null) {
            emergencyFundBalance = BigDecimal.ZERO;
        }
    }
    
    // Helper method to calculate total balance from accounts
    public void calculateTotalBalanceFromAccounts(List<Account> accounts) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal savings = BigDecimal.ZERO;
        BigDecimal emergency = BigDecimal.ZERO;
        
        for (Account account : accounts) {
            if (account.getType().equalsIgnoreCase("bank")) {
                total = total.add(account.getBalance());
            } else if (account.getType().equalsIgnoreCase("savings")) {
                savings = savings.add(account.getBalance());
                total = total.add(account.getBalance());
            } else if (account.getName().toLowerCase().contains("emergency")) {
                emergency = emergency.add(account.getBalance());
                total = total.add(account.getBalance());
            } else {
                total = total.add(account.getBalance());
            }
        }
        
        this.totalBalance = total;
        this.savingsBalance = savings;
        this.emergencyFundBalance = emergency;
    }
}