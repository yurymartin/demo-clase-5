package com.fintech.core.payments.repository;

import com.fintech.core.payments.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    
    @Query(value = "SELECT * FROM users WHERE username = '" + ":username" + "' AND password = '" + ":password" + "'", nativeQuery = true)
    User findByUsernameAndPasswordUnsafe(@Param("username") String username, @Param("password") String password);
    
    @Query(value = "SELECT u.*, u.ssn, u.credit_card_number, u.account_number FROM users u WHERE u.balance > ?1", nativeQuery = true)
    List<User> findUsersWithHighBalance(Double minBalance);
    
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN'")
    List<User> findAllAdminUsers();
    
    List<User> findByFirstNameContainingIgnoreCase(String firstName);
    List<User> findByLastNameContainingIgnoreCase(String lastName);
    List<User> findByIsActiveTrue();
    List<User> findByIsActiveFalse();
}
