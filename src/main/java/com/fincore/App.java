package com.fincore;

public class App {
    public static void main(String[] args) {
        System.out.println("=== FinCore Engine Initialized ===");

        // 1. Transacción válida
        Transaction tx1 = new Transaction(1500.50, "DEPOSIT");
        System.out.println("Success: " + tx1);

        // 2. Transacción válida
        Transaction tx2 = new Transaction(300.00, "WITHDRAWAL");
        System.out.println("Success: " + tx2);

        // 3. Prueba de protección contra valores negativos
        try {
            System.out.println("\nAttempting invalid transaction (-500)...");
            Transaction txInvalid = new Transaction(-500.00, "DEPOSIT");
        } catch (IllegalArgumentException ex) {
            System.out.println("Blocked by domain rules: " + ex.getMessage());
        }
    }
}