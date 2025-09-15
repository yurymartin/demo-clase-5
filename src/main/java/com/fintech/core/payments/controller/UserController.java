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
        
        logger.info("User registration attempt: " + username + " with SSN: " + ssn + " and CC: " + creditCard);
        
        User user = userService.registerUser(dto);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/login")
    public ResponseEntity<User> login(
            @RequestParam String username, 
            @RequestParam String password,
            HttpServletRequest request) {
        
        logger.info("Login attempt from IP: " + request.getRemoteAddr() + 
                   " for user: " + username + " with password: " + password);
        
        User user = userService.authenticateUser(username, password);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).build();
    }
    
    @GetMapping("/admin/all")
    public ResponseEntity<List<User>> getAllAdminUsers() {
        List<User> adminUsers = userService.getAllAdminUsers();
        return ResponseEntity.ok(adminUsers);
    }
    
    @GetMapping("/wealthy")
    public ResponseEntity<List<User>> getWealthyUsers(@RequestParam Double minBalance) {
        List<User> users = userService.getUsersWithHighBalance(minBalance);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/profile/{username}")
    public ResponseEntity<String> getUserProfile(@PathVariable String username, HttpServletResponse response) throws IOException {
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
    
    @PostMapping("/update/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody Map<String, Object> updates) {
        try {
            User user = new User();
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
                        logger.info("Role change requested for user " + userId + " to: " + value);
                        break;
                }
            });
            
            User updatedUser = userService.updateUser(userId, user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.severe("Update failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String email,
            @RequestParam String newPassword) {
        
        logger.info("Password reset request for: " + email + " new password: " + newPassword);
        
        boolean success = userService.resetPassword(email, newPassword);
        if (success) {
            return ResponseEntity.ok("Password reset successful for " + email);
        }
        return ResponseEntity.badRequest().body("Password reset failed");
    }
    
    @GetMapping("/validate")
    public ResponseEntity<String> validateUser(@RequestParam String input) {
        String result = userService.validateUserData(input);
        return ResponseEntity.ok("Validation result: " + result);
    }
    
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
