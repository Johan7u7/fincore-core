package com.fincore;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Transaction {
    private final String id;
    private final double amount;
    private final String type; // "DEPOSIT" or "WITHDRAWAL"
    private final LocalDateTime timestamp;

    public Transaction(double amount, String type) {
        // Regla 1: El monto debe ser estrictamente positivo
        if (amount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be strictly greater than zero.");
        }

        // Regla 2: Solo se aceptan tipos válidos
        if (!"DEPOSIT".equalsIgnoreCase(type) && !"WITHDRAWAL".equalsIgnoreCase(type)) {
            throw new IllegalArgumentException("Invalid transaction type. Allowed: DEPOSIT, WITHDRAWAL.");
        }

        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.type = type.toUpperCase();
        this.timestamp = LocalDateTime.now();
    }

    // Solo métodos Getters (Inmutabilidad)
    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Contrato de identidad empresarial (Basado en el ID único)
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Transaction[ID=%s, Amount=%.2f, Type=%s, Date=%s]",
                id.substring(0, 8) + "...", amount, type, timestamp);
    }
}