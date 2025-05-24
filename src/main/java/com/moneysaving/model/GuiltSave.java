package com.moneysaving.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "guilt_save")
public class GuiltSave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 50)
    private String category;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "save_percentage", precision = 5, scale = 2)
    private BigDecimal savePercentage;
    
    @Column(name = "destination_account", length = 50)
    private String destinationAccount;
    
    @Column(name = "active")
    private Boolean isActive;
    
    @Column(name = "saved_amount", precision = 10, scale = 2)
    private BigDecimal savedAmount;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 