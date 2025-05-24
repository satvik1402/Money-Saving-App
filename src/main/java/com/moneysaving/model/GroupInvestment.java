package com.moneysaving.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "group_investment_pots")
public class GroupInvestment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String name;
    
    @Column(length = 100)
    private String goal;
    
    @Column(name = "monthly_contribution", precision = 10, scale = 2)
    private BigDecimal monthlyContribution;
    
    @Column(name = "investment_type", length = 50)
    private String investmentType;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "group_members",
        joinColumns = @JoinColumn(name = "pot_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members;
} 