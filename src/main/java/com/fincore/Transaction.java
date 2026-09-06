package com.fincore;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Transaction {
    private final String id;
    private final BigDecimal amount;
    private final TransactionType type;
    private final LocalDateTime timestamp;

    public Transaction(BigDecimal amount, TransactionType type) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be strictly greater than zero.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null.");
        }

        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Transaction[ID=%s, Amount=%s, Type=%s, Date=%s]",
                id.substring(0, 8) + "...", amount.toPlainString(), type, timestamp);
    }
}