package com.fincore;

import java.util.*;

public class LedgerService {

    // Índice en memoria: cada cuenta tiene su lista histórica de transacciones
    private final Map<String, List<Transaction>> ledger = new HashMap<>();

    /**
     * Registra un depósito en la cuenta. Si la cuenta no existe, la inicializa.
     */
    public Transaction deposit(String accountId, double amount) {
        Transaction tx = new Transaction(amount, TransactionType.DEPOSIT);
        recordTransaction(accountId, tx);
        return tx;
    }

    /**
     * Valida fondos disponibles antes de autorizar y registrar un retiro.
     */
    public Transaction withdraw(String accountId, double amount) {
        double currentBalance = getBalance(accountId);
        if (currentBalance < amount) {
            throw new InsufficientFundsException(
                String.format("Overdraft blocked: Current balance is %.2f, attempted withdrawal of %.2f", 
                              currentBalance, amount)
            );
        }

        Transaction tx = new Transaction(amount, TransactionType.WITHDRAWAL);
        recordTransaction(accountId, tx);
        return tx;
    }

    /**
     * Calcula el saldo neto en tiempo real sumando depósitos y restando retiros.
     */
    public double getBalance(String accountId) {
        List<Transaction> history = ledger.getOrDefault(accountId, Collections.emptyList());
        
        double balance = 0.0;
        for (Transaction tx : history) {
            if (tx.getType() == TransactionType.DEPOSIT) {
                balance += tx.getAmount();
            } else if (tx.getType() == TransactionType.WITHDRAWAL) {
                balance -= tx.getAmount();
            }
        }
        return balance;
    }

    /**
     * Devuelve una copia inmutable del historial para proteger la integridad del libro contable.
     */
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
}