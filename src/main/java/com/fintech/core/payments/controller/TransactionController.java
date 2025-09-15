package com.fintech.core.payments.controller;

import com.fintech.core.payments.dto.TransactionDTO;
import com.fintech.core.payments.entity.Transaction;
import com.fintech.core.payments.entity.TransactionStatus;
import com.fintech.core.payments.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    private static final Logger logger = Logger.getLogger(TransactionController.class.getName());
    
    @Autowired
    private TransactionService transactionService;
    
    // VULNERABILITY: No authentication or authorization checks
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> createTransfer(@RequestBody TransactionDTO dto, @RequestParam Long userId) {
        try {
            // VULNERABILITY: Logging sensitive transaction data
            logger.info(String.format("Transfer request: User %d, From %d to %d, Amount: %.2f, PIN: %s", 
                       userId, dto.getFromAccountId(), dto.getToAccountId(), dto.getAmount(), dto.getPin()));
            
            Transaction transaction = transactionService.createTransaction(dto, userId);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            // VULNERABILITY: Exposing internal error details
            logger.severe("Transfer failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    // VULNERABILITY: No access control - any user can view any user's transactions
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getUserTransactions(@PathVariable Long userId) {
        List<Transaction> transactions = transactionService.getTransactionsByUser(userId);
        return ResponseEntity.ok(transactions);
    }
    
    // VULNERABILITY: Exposing sensitive financial data without proper authorization
    @GetMapping("/high-value/{userId}")
    public ResponseEntity<List<Transaction>> getHighValueTransactions(
            @PathVariable Long userId, 
            @RequestParam Double minAmount) {
        
        // VULNERABILITY: Logging sensitive query parameters
        logger.info("High value transaction query for user " + userId + " with minimum amount: " + minAmount);
        
        List<Transaction> transactions = transactionService.getHighValueTransactions(userId, minAmount);
        return ResponseEntity.ok(transactions);
    }
    
    // VULNERABILITY: Admin function exposed without authentication
    @GetMapping("/admin/sensitive-data")
    public ResponseEntity<List<Transaction>> getAllSensitiveTransactions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<Transaction> transactions = transactionService.getAllTransactionsWithSensitiveData(startDate, endDate);
        return ResponseEntity.ok(transactions);
    }
    
    // VULNERABILITY: No authorization check for transaction status updates
    @PutMapping("/status/{transactionId}")
    public ResponseEntity<Transaction> updateTransactionStatus(
            @PathVariable Long transactionId,
            @RequestParam TransactionStatus status,
            @RequestParam(required = false) String reason) {
        
        Transaction transaction = transactionService.updateTransactionStatus(transactionId, status, reason);
        if (transaction != null) {
            return ResponseEntity.ok(transaction);
        }
        return ResponseEntity.notFound().build();
    }
    
    // VULNERABILITY: Business logic flaw - refund without proper validation
    @PostMapping("/refund/{transactionId}")
    public ResponseEntity<Transaction> processRefund(
            @PathVariable Long transactionId,
            @RequestParam Double refundAmount,
            HttpServletRequest request) {
        
        try {
            // VULNERABILITY: Logging refund details that could be sensitive
            logger.info(String.format("Refund request from IP %s for transaction %d, amount: %.2f", 
                       request.getRemoteAddr(), transactionId, refundAmount));
            
            Transaction refund = transactionService.processRefund(transactionId, refundAmount);
            return ResponseEntity.ok(refund);
        } catch (Exception e) {
            // VULNERABILITY: Exposing internal error messages
            logger.severe("Refund processing failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // VULNERABILITY: SQL Injection through direct parameter passing
    @GetMapping("/search")
    public ResponseEntity<String> searchTransactions(
            @RequestParam String query,
            @RequestParam(required = false) String userId) {
        
        // VULNERABILITY: Direct query execution without sanitization
        String searchQuery = "SELECT * FROM transactions WHERE description LIKE '%" + query + "%'";
        if (userId != null) {
            searchQuery += " AND user_id = " + userId;
        }
        
        // VULNERABILITY: Logging potentially malicious input
        logger.info("Search query executed: " + searchQuery);
        
        return ResponseEntity.ok("Search completed for: " + query);
    }
    
    // VULNERABILITY: XXE (XML External Entity) vulnerability
    @PostMapping("/import")
    public ResponseEntity<String> importTransactions(@RequestBody String xmlData) {
        try {
            // VULNERABILITY: Processing XML without secure configuration
            logger.info("Processing XML transaction import: " + xmlData);
            
            // This would normally parse XML data unsafely
            return ResponseEntity.ok("Import completed successfully");
        } catch (Exception e) {
            logger.severe("XML import failed: " + e.getMessage());
            return ResponseEntity.badRequest().body("Import failed: " + e.getMessage());
        }
    }
    
    // VULNERABILITY: Insecure deserialization
    @PostMapping("/bulk-update")
    public ResponseEntity<String> bulkUpdateTransactions(@RequestBody Object transactionUpdates) {
        try {
            // VULNERABILITY: Processing untrusted serialized data
            logger.info("Bulk update request: " + transactionUpdates.toString());
            
            return ResponseEntity.ok("Bulk update completed");
        } catch (Exception e) {
            // VULNERABILITY: Exposing stack trace
            logger.severe("Bulk update failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Update failed");
        }
    }
    
    // VULNERABILITY: Information disclosure through debug endpoint
    @GetMapping("/debug/all")
    public ResponseEntity<String> debugAllTransactions() {
        // VULNERABILITY: No authentication for sensitive debug info
        return ResponseEntity.ok("Debug endpoint - exposing all transaction data would go here");
    }
}
