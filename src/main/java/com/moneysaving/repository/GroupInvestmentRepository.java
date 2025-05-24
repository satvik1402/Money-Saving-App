package com.moneysaving.repository;

import com.moneysaving.model.GroupInvestment;
import com.moneysaving.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroupInvestmentRepository extends JpaRepository<GroupInvestment, Long> {
    List<GroupInvestment> findByIsActiveTrue();
    
    List<GroupInvestment> findByMembersContainingAndIsActiveTrue(User user);
    
    @Query("SELECT g FROM GroupInvestment g JOIN g.members m WHERE m = ?1 AND g.isActive = true")
    List<GroupInvestment> findActiveGroupsByMember(User user);
    
    @Query("SELECT g FROM GroupInvestment g WHERE g.investmentType = ?1 AND g.isActive = true")
    List<GroupInvestment> findByInvestmentType(String investmentType);
    
    boolean existsByNameAndIsActiveTrue(String name);

    List<GroupInvestment> findByMembersContaining(User user);
} 