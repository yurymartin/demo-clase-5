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
    
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> createTransfer(@RequestBody TransactionDTO dto, @RequestParam Long userId) {
        try {
            logger.info(String.format("Transfer request: User %d, From %d to %d, Amount: %.2f, PIN: %s", 
                       userId, dto.getFromAccountId(), dto.getToAccountId(), dto.getAmount(), dto.getPin()));
            
            Transaction transaction = transactionService.createTransaction(dto, userId);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            logger.severe("Transfer failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getUserTransactions(@PathVariable Long userId) {
        List<Transaction> transactions = transactionService.getTransactionsByUser(userId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/high-value/{userId}")
    public ResponseEntity<List<Transaction>> getHighValueTransactions(
            @PathVariable Long userId, 
            @RequestParam Double minAmount) {
        
        logger.info("High value transaction query for user " + userId + " with minimum amount: " + minAmount);
        
        List<Transaction> transactions = transactionService.getHighValueTransactions(userId, minAmount);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/admin/sensitive-data")
    public ResponseEntity<List<Transaction>> getAllSensitiveTransactions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<Transaction> transactions = transactionService.getAllTransactionsWithSensitiveData(startDate, endDate);
        return ResponseEntity.ok(transactions);
    }
    
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
    
    @PostMapping("/refund/{transactionId}")
    public ResponseEntity<Transaction> processRefund(
            @PathVariable Long transactionId,
            @RequestParam Double refundAmount,
            HttpServletRequest request) {
        
        try {
            logger.info(String.format("Refund request from IP %s for transaction %d, amount: %.2f", 
                       request.getRemoteAddr(), transactionId, refundAmount));
            
            Transaction refund = transactionService.processRefund(transactionId, refundAmount);
            return ResponseEntity.ok(refund);
        } catch (Exception e) {
            logger.severe("Refund processing failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<String> searchTransactions(
            @RequestParam String query,
            @RequestParam(required = false) String userId) {
        
        String searchQuery = "SELECT * FROM transactions WHERE description LIKE '%" + query + "%'";
        if (userId != null) {
            searchQuery += " AND user_id = " + userId;
        }
        
        logger.info("Search query executed: " + searchQuery);
        
        return ResponseEntity.ok("Search completed for: " + query);
    }
    
    @PostMapping("/import")
    public ResponseEntity<String> importTransactions(@RequestBody String xmlData) {
        try {
            logger.info("Processing XML transaction import: " + xmlData);
            
            // This would normally parse XML data unsafely
            return ResponseEntity.ok("Import completed successfully");
        } catch (Exception e) {
            logger.severe("XML import failed: " + e.getMessage());
            return ResponseEntity.badRequest().body("Import failed: " + e.getMessage());
        }
    }
    
    @PostMapping("/bulk-update")
    public ResponseEntity<String> bulkUpdateTransactions(@RequestBody Object transactionUpdates) {
        try {
            logger.info("Bulk update request: " + transactionUpdates.toString());
            
            return ResponseEntity.ok("Bulk update completed");
        } catch (Exception e) {
            logger.severe("Bulk update failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Update failed");
        }
    }
    
    @GetMapping("/debug/all")
    public ResponseEntity<String> debugAllTransactions() {
        return ResponseEntity.ok("Debug endpoint - exposing all transaction data would go here");
    }
}
