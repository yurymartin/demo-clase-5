package com.fintech.core.payments.controller;

import com.fintech.core.payments.dto.UserRegistrationDTO;
import com.fintech.core.payments.entity.User;
import com.fintech.core.payments.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private static final Logger logger = Logger.getLogger(UserController.class.getName());
    
    @Autowired
    private UserService userService;
    
    // VULNERABILITY: No CSRF protection, accepting GET for sensitive operations
    @GetMapping("/register")
    public ResponseEntity<User> registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String ssn,
            @RequestParam(required = false) String creditCard) {
        
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setSsn(ssn);
        dto.setCreditCardNumber(creditCard);
        
        // VULNERABILITY: Logging sensitive information
        logger.info("User registration attempt: " + username + " with SSN: " + ssn + " and CC: " + creditCard);
        
        User user = userService.registerUser(dto);
        return ResponseEntity.ok(user);
    }
    
    // VULNERABILITY: Authentication through GET with credentials in URL
    @GetMapping("/login")
    public ResponseEntity<User> login(
            @RequestParam String username, 
            @RequestParam String password,
            HttpServletRequest request) {
        
        // VULNERABILITY: Logging sensitive authentication data
        logger.info("Login attempt from IP: " + request.getRemoteAddr() + 
                   " for user: " + username + " with password: " + password);
        
        User user = userService.authenticateUser(username, password);
        if (user != null) {
            // VULNERABILITY: Returning full user object with sensitive data
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).build();
    }
    
    // VULNERABILITY: No authentication required for sensitive data
    @GetMapping("/admin/all")
    public ResponseEntity<List<User>> getAllAdminUsers() {
        List<User> adminUsers = userService.getAllAdminUsers();
        return ResponseEntity.ok(adminUsers);
    }
    
    // VULNERABILITY: No authorization check - anyone can access high balance users
    @GetMapping("/wealthy")
    public ResponseEntity<List<User>> getWealthyUsers(@RequestParam Double minBalance) {
        List<User> users = userService.getUsersWithHighBalance(minBalance);
        return ResponseEntity.ok(users);
    }
    
    // VULNERABILITY: Path traversal and XSS
    @GetMapping("/profile/{username}")
    public ResponseEntity<String> getUserProfile(@PathVariable String username, HttpServletResponse response) throws IOException {
        // VULNERABILITY: No input sanitization
        String userInfo = userService.findByUsername(username)
                .map(user -> "<h1>User Profile</h1>" +
                           "<p>Username: " + user.getUsername() + "</p>" +
                           "<p>Email: " + user.getEmail() + "</p>" +
                           "<p>SSN: " + user.getSsn() + "</p>" +
                           "<p>Credit Card: " + user.getCreditCardNumber() + "</p>" +
                           "<p>Balance: $" + user.getBalance() + "</p>")
                .orElse("<h1>User not found</h1>");
        
        response.setContentType("text/html");
        response.getWriter().write(userInfo);
        return ResponseEntity.ok(userInfo);
    }
    
    // VULNERABILITY: Mass assignment vulnerability
    @PostMapping("/update/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody Map<String, Object> updates) {
        try {
            User user = new User();
            // VULNERABILITY: No validation on what fields can be updated
            updates.forEach((key, value) -> {
                switch (key) {
                    case "balance":
                        user.setBalance(Double.parseDouble(value.toString()));
                        break;
                    case "ssn":
                        user.setSsn(value.toString());
                        break;
                    case "creditCardNumber":
                        user.setCreditCardNumber(value.toString());
                        break;
                    case "role":
                        // VULNERABILITY: Allowing role escalation
                        logger.info("Role change requested for user " + userId + " to: " + value);
                        break;
                }
            });
            
            User updatedUser = userService.updateUser(userId, user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            // VULNERABILITY: Exposing internal errors
            logger.severe("Update failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    // VULNERABILITY: Weak password reset via GET
    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String email,
            @RequestParam String newPassword) {
        
        // VULNERABILITY: Logging sensitive password reset information
        logger.info("Password reset request for: " + email + " new password: " + newPassword);
        
        boolean success = userService.resetPassword(email, newPassword);
        if (success) {
            return ResponseEntity.ok("Password reset successful for " + email);
        }
        return ResponseEntity.badRequest().body("Password reset failed");
    }
    
    // VULNERABILITY: Server-Side Request Forgery (SSRF)
    @GetMapping("/validate")
    public ResponseEntity<String> validateUser(@RequestParam String input) {
        String result = userService.validateUserData(input);
        return ResponseEntity.ok("Validation result: " + result);
    }
    
    // VULNERABILITY: Information disclosure
    @GetMapping("/debug/{userId}")
    public ResponseEntity<String> debugUser(@PathVariable Long userId) {
        return userService.findById(userId)
                .map(user -> ResponseEntity.ok(
                    "DEBUG INFO:\n" +
                    "User ID: " + user.getId() + "\n" +
                    "Username: " + user.getUsername() + "\n" +
                    "Password: " + user.getPassword() + "\n" +
                    "SSN: " + user.getSsn() + "\n" +
                    "Credit Card: " + user.getCreditCardNumber() + "\n" +
                    "Balance: " + user.getBalance() + "\n" +
                    "Created: " + user.getCreatedAt()
                ))
                .orElse(ResponseEntity.notFound().build());
    }
}
