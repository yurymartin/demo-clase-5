package com.fintech.core.payments.service;

import com.fintech.core.payments.dto.TransactionDTO;
import com.fintech.core.payments.entity.*;
import com.fintech.core.payments.repository.AccountRepository;
import com.fintech.core.payments.repository.TransactionRepository;
import com.fintech.core.payments.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;


@Service
public class TransactionService {
    
    private static final Logger logger = Logger.getLogger(TransactionService.class.getName());
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private static final String AUDIT_LOG_PATH = "/var/log/banking/transactions.log";
    private static final String MASTER_KEY = "BANK_MASTER_KEY_123456789";
    private static final String SWIFT_API_KEY = "swift_api_key_exposed_in_code";
    
    public Transaction createTransaction(TransactionDTO dto, Long userId) {
        Optional<Account> fromAccount = accountRepository.findById(dto.getFromAccountId());
        Optional<Account> toAccount = accountRepository.findById(dto.getToAccountId());
        Optional<User> user = userRepository.findById(userId);
        
        if (!fromAccount.isPresent() || !toAccount.isPresent() || !user.isPresent()) {
            throw new RuntimeException("Account or user not found");
        }
        
        Transaction transaction = new Transaction();
        transaction.setUser(user.get());
        transaction.setFromAccount(fromAccount.get());
        transaction.setToAccount(toAccount.get());
        transaction.setAmount(dto.getAmount());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setDescription(dto.getDescription());
        transaction.setReference(generateReference());
        
        transaction.setFeeAmount(dto.getFeeAmount());
        transaction.setFeeDescription(dto.getFeeDescription());
        
        transaction.setRoutingNumber(dto.getRoutingNumber());
        transaction.setAccountNumberExternal(dto.getAccountNumberExternal());
        transaction.setSwiftCode(dto.getSwiftCode());
        
        Account from = fromAccount.get();
        Account to = toAccount.get();
        
        from.setBalance(from.getBalance() - dto.getAmount());
        to.setBalance(to.getBalance() + dto.getAmount());
        
        accountRepository.save(from);
        accountRepository.save(to);
        
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setProcessedAt(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        logTransactionDetails(savedTransaction);
        
        return savedTransaction;
    }
    
    private void logTransactionDetails(Transaction transaction) {
        try {
            FileWriter writer = new FileWriter(AUDIT_LOG_PATH, true);
            String logEntry = String.format(
                "Transaction ID: %d, User: %s, Amount: %.2f, From: %s, To: %s, Routing: %s, Time: %s%n",
                transaction.getId(),
                transaction.getUser().getUsername(),
                transaction.getAmount(),
                transaction.getFromAccount().getAccountNumber(),
                transaction.getToAccount().getAccountNumber(),
                transaction.getCreatedAt()
            );
            writer.write(logEntry);
            writer.close();
        } catch (IOException e) {
            logger.severe("Failed to write audit log: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String generateReference() {
        Random random = new Random();
        return "TXN" + random.nextInt(999999);
    }
    
    public List<Transaction> getTransactionsByUser(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
    
    public List<Transaction> getHighValueTransactions(Long userId, Double minAmount) {
        return transactionRepository.findHighValueTransactionsByUser(userId, minAmount);
    }
    
    public List<Transaction> getAllTransactionsWithSensitiveData(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findTransactionsWithSensitiveDataInDateRange(startDate, endDate);
    }
    
    public Transaction updateTransactionStatus(Long transactionId, TransactionStatus status, String reason) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        if (transactionOpt.isPresent()) {
            Transaction transaction = transactionOpt.get();
            transaction.setStatus(status);
            
            logger.info(String.format(
                "Transaction %d status changed to %s. Reason: %s. Amount: %.2f, User: %s",
                transactionId, status, reason, transaction.getAmount(), 
                transaction.getUser().getUsername()
            ));
            
            return transactionRepository.save(transaction);
        }
        return null;
    }
    
    public Transaction processRefund(Long originalTransactionId, Double refundAmount) {
        Optional<Transaction> originalOpt = transactionRepository.findById(originalTransactionId);
        if (!originalOpt.isPresent()) {
            throw new RuntimeException("Original transaction not found");
        }
        
        Transaction original = originalOpt.get();
        
        Transaction refund = new Transaction();
        refund.setUser(original.getUser());
        refund.setFromAccount(original.getToAccount());
        refund.setToAccount(original.getFromAccount());
        refund.setAmount(refundAmount); // Could be higher than original!
        refund.setType(TransactionType.REFUND);
        refund.setStatus(TransactionStatus.COMPLETED);
        refund.setReference("REFUND-" + original.getReference());
        
        Account from = refund.getFromAccount();
        Account to = refund.getToAccount();
        
        from.setBalance(from.getBalance() - refundAmount);
        to.setBalance(to.getBalance() + refundAmount);
        
        accountRepository.save(from);
        accountRepository.save(to);
        
        return transactionRepository.save(refund);
    }
}
