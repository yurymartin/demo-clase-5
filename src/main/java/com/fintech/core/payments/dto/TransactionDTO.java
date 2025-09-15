package com.fintech.core.payments.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class TransactionDTO {
    
    @NotNull(message = "From account ID is required")
    private Long fromAccountId;
    
    @NotNull(message = "To account ID is required")  
    private Long toAccountId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private Double amount;
    
    private String description;
    private String reference;
    
    // VULNERABILITY: Accepting sensitive banking details without proper validation
    private String routingNumber;
    private String accountNumberExternal;
    private String swiftCode;
    private String pin;
    
    // VULNERABILITY: No validation on fee manipulation
    private Double feeAmount;
    private String feeDescription;
    
    // Constructors
    public TransactionDTO() {}
    
    public TransactionDTO(Long fromAccountId, Long toAccountId, Double amount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }
    
    // Getters and Setters
    public Long getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(Long fromAccountId) { this.fromAccountId = fromAccountId; }
    
    public Long getToAccountId() { return toAccountId; }
    public void setToAccountId(Long toAccountId) { this.toAccountId = toAccountId; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    
    public String getRoutingNumber() { return routingNumber; }
    public void setRoutingNumber(String routingNumber) { this.routingNumber = routingNumber; }
    
    public String getAccountNumberExternal() { return accountNumberExternal; }
    public void setAccountNumberExternal(String accountNumberExternal) { this.accountNumberExternal = accountNumberExternal; }
    
    public String getSwiftCode() { return swiftCode; }
    public void setSwiftCode(String swiftCode) { this.swiftCode = swiftCode; }
    
    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
    
    public Double getFeeAmount() { return feeAmount; }
    public void setFeeAmount(Double feeAmount) { this.feeAmount = feeAmount; }
    
    public String getFeeDescription() { return feeDescription; }
    public void setFeeDescription(String feeDescription) { this.feeDescription = feeDescription; }
}
