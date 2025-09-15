package com.fintech.core.payments.repository;

import com.fintech.core.payments.entity.Account;
import com.fintech.core.payments.entity.AccountStatus;
import com.fintech.core.payments.entity.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByUserId(Long userId);
    List<Account> findByType(AccountType type);
    List<Account> findByStatus(AccountStatus status);
    
    @Query(value = "SELECT * FROM accounts WHERE account_number = '" + ":accountNumber" + "'", nativeQuery = true)
    Account findByAccountNumberUnsafe(@Param("accountNumber") String accountNumber);
    
    @Query(value = "SELECT a.*, a.pin, a.security_code, a.mother_maiden_name FROM accounts a WHERE a.user_id = ?1", nativeQuery = true)
    List<Account> findAccountsWithSensitiveDataByUserId(Long userId);
    
    @Query("SELECT a FROM Account a WHERE a.balance > :minBalance ORDER BY a.balance DESC")
    List<Account> findAccountsWithHighBalance(@Param("minBalance") Double minBalance);
    
    List<Account> findByBalanceBetween(Double minBalance, Double maxBalance);
    List<Account> findByUserIdAndStatus(Long userId, AccountStatus status);
}
