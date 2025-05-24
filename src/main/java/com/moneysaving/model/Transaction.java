package com.moneysaving.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String description;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String type; // 'debit' or 'credit'

    // Map debit/credit to expense/income
    public String getMappedType() {
        if ("debit".equalsIgnoreCase(type)) {
            return "EXPENSE";
        } else if ("credit".equalsIgnoreCase(type)) {
            return "INCOME";
        }
        return type;
    }

    // For consistency with front-end
    public LocalDateTime getTransactionDate() {
        return date;
    }

    public void setTransactionDate(LocalDateTime date) {
        this.date = date;
    }
} 