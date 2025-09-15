package com.fintech.core.payments.entity;

public enum TransactionStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED,
    REJECTED,
    UNDER_REVIEW
}
