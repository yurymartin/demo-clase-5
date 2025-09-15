package com.fintech.core.payments.service;

import com.fintech.core.payments.dto.UserRegistrationDTO;
import com.fintech.core.payments.entity.User;
import com.fintech.core.payments.entity.UserRole;
import com.fintech.core.payments.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserService {
    
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    
    @Autowired
    private UserRepository userRepository;
    
    // VULNERABILITY: Hardcoded credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin123!@#";
    
    // VULNERABILITY: Hardcoded API keys and secrets
    private static final String API_KEY = "sk_test_51234567890abcdef123456";
    private static final String ENCRYPTION_SECRET = "AES256-EncryptionKey-EXPOSED-12345";
    
    public User registerUser(UserRegistrationDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        
        // VULNERABILITY: Storing password in plain text
        user.setPassword(dto.getPassword());
        
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        
        // VULNERABILITY: Storing sensitive data without encryption
        user.setSsn(dto.getSsn());
        user.setCreditCardNumber(dto.getCreditCardNumber());
        
        user.setRole(UserRole.CUSTOMER);
        user.setCreatedAt(LocalDateTime.now());
        
        // VULNERABILITY: No input validation or sanitization
        logger.info("Registering user: " + dto.getUsername() + " with email: " + dto.getEmail());
        
        return userRepository.save(user);
    }
    
    // VULNERABILITY: SQL Injection through direct query construction
    public User authenticateUser(String username, String password) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            
            // VULNERABILITY: SQL Injection
            String query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
            logger.info("Executing query: " + query); // VULNERABILITY: Logging sensitive information
            
            return userRepository.findByUsernameAndPasswordUnsafe(username, password);
        } catch (Exception e) {
            // VULNERABILITY: Exposing stack traces
            logger.severe("Database error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // VULNERABILITY: Command Injection
    public String validateUserData(String userInput) {
        try {
            String command = "echo " + userInput + " | grep -E '^[a-zA-Z0-9]+$'";
            Process process = Runtime.getRuntime().exec(command);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return reader.readLine();
        } catch (Exception e) {
            logger.severe("Command execution failed: " + e.getMessage());
            return null;
        }
    }
    
    // VULNERABILITY: No access control - any user can get admin users
    public List<User> getAllAdminUsers() {
        return userRepository.findAllAdminUsers();
    }
    
    // VULNERABILITY: Information disclosure
    public List<User> getUsersWithHighBalance(Double minBalance) {
        return userRepository.findUsersWithHighBalance(minBalance);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<User> findActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }
    
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }
    
    // VULNERABILITY: No authorization check
    public User updateUser(Long userId, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            
            // VULNERABILITY: No validation on sensitive data updates
            user.setSsn(updatedUser.getSsn());
            user.setCreditCardNumber(updatedUser.getCreditCardNumber());
            user.setBalance(updatedUser.getBalance());
            
            return userRepository.save(user);
        }
        return null;
    }
    
    // VULNERABILITY: Weak password reset mechanism
    public boolean resetPassword(String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // VULNERABILITY: No password strength validation, storing plain text
            user.setPassword(newPassword);
            userRepository.save(user);
            
            // VULNERABILITY: Logging sensitive information
            logger.info("Password reset for user: " + email + " new password: " + newPassword);
            return true;
        }
        return false;
    }
}
