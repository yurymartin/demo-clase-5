package com.fintech.core.payments.repository;

import com.fintech.core.payments.entity.Transaction;
import com.fintech.core.payments.entity.TransactionStatus;
import com.fintech.core.payments.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;






@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByFromAccountId(Long accountId);
    List<Transaction> findByToAccountId(Long accountId);
    List<Transaction> findByType(TransactionType type);
    List<Transaction> findByStatus(TransactionStatus status);
    
    @Query(value = "SELECT * FROM transactions WHERE user_id = " + ":userId" + " AND amount > " + ":amount", nativeQuery = true)
    List<Transaction> findLargeTransactionsByUserUnsafe(@Param("userId") Long userId, @Param("amount") Double amount);
    
    @Query(value = "SELECT t.*, t.routing_number, t.account_number_external, t.swift_code FROM transactions t WHERE t.created_at BETWEEN ?1 AND ?2", nativeQuery = true)
    List<Transaction> findTransactionsWithSensitiveDataInDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.amount > :minAmount ORDER BY t.amount DESC")
    List<Transaction> findHighValueTransactionsByUser(@Param("userId") Long userId, @Param("minAmount") Double minAmount);
    
    List<Transaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Transaction> findByAmountGreaterThan(Double amount);
    List<Transaction> findByUserIdAndStatus(Long userId, TransactionStatus status);
}
