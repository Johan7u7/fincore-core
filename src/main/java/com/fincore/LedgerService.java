package com.fincore;

import java.math.BigDecimal;
import java.util.*;

public class LedgerService {

    private final Map<String, List<Transaction>> ledger = new HashMap<>();

    public Transaction deposit(String accountId, BigDecimal amount) {
        Transaction tx = new Transaction(amount, TransactionType.DEPOSIT);
        recordTransaction(accountId, tx);
        return tx;
    }

    public Transaction withdraw(String accountId, BigDecimal amount) {
        BigDecimal currentBalance = getBalance(accountId);
        if (currentBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    String.format("Overdraft blocked: Current balance is %s, attempted withdrawal of %s",
                            currentBalance.toPlainString(), amount.toPlainString()));
        }

        Transaction tx = new Transaction(amount, TransactionType.WITHDRAWAL);
        recordTransaction(accountId, tx);
        return tx;
    }

    public BigDecimal getBalance(String accountId) {
        List<Transaction> history = ledger.getOrDefault(accountId, Collections.emptyList());

        BigDecimal balance = BigDecimal.ZERO;
        for (Transaction tx : history) {
            if (tx.getType() == TransactionType.DEPOSIT) {
                balance = balance.add(tx.getAmount());
            } else if (tx.getType() == TransactionType.WITHDRAWAL) {
                balance = balance.subtract(tx.getAmount());
            }
        }
        return balance;
    }

    public List<Transaction> getHistory(String accountId) {
        List<Transaction> history = ledger.get(accountId);
        if (history == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(history);
    }

    private void recordTransaction(String accountId, Transaction tx) {
        ledger.computeIfAbsent(accountId, k -> new ArrayList<>()).add(tx);
    }

    /**
     * Ejecuta una transferencia atómica entre dos cuentas.
     * Si falla el abono al destino, compensa automáticamente el débito del origen
     * (Rollback).
     */
    public void transfer(String sourceAccountId, String destinationAccountId, BigDecimal amount) {
        if (sourceAccountId == null || destinationAccountId == null) {
            throw new IllegalArgumentException("Account IDs cannot be null.");
        }
        if (sourceAccountId.equals(destinationAccountId)) {
            throw new IllegalArgumentException("Cannot transfer funds to the same account.");
        }

        // Paso 1: Débito de la cuenta origen (valida fondos y arroja
        // InsufficientFundsException si no alcanza)
        Transaction debitTx = withdraw(sourceAccountId, amount);

        // Paso 2: Abono a la cuenta destino con guardia de compensación
        try {
            deposit(destinationAccountId, amount);
        } catch (Exception e) {
            // ROLLBACK: Compensar acreditando de vuelta el monto al origen
            deposit(sourceAccountId, amount);
            throw new IllegalStateException(
                    "Transfer failed during destination credit. Rollback executed successfully.", e);
        }
    }
}