package com.fincore;

public class App {
    public static void main(String[] args) {
        System.out.println("=== FinCore Ledger Service Demo ===");

        LedgerService service = new LedgerService();
        String accountId = "ACC-001";

        // 1. Depósito inicial
        service.deposit(accountId, 1000.00);
        System.out.println("Deposit: +1000.00 | Balance: " + service.getBalance(accountId));

        // 2. Retiro válido
        service.withdraw(accountId, 450.00);
        System.out.println("Withdrawal: -450.00 | Balance: " + service.getBalance(accountId));

        // 3. Intento de sobregiro (fondos insuficientes)
        System.out.println("\nAttempting to withdraw 600.00 (Available: " + service.getBalance(accountId) + ")...");
        try {
            service.withdraw(accountId, 600.00);
        } catch (InsufficientFundsException e) {
            System.out.println("Blocked by domain rules: " + e.getMessage());
        }

        // 4. Verificación de balance final y auditoría
        System.out.println("\nFinal verified balance: " + service.getBalance(accountId));
        System.out.println("Transaction count: " + service.getHistory(accountId).size());
    }
}