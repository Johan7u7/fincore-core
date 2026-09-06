package com.fincore;

import java.math.BigDecimal;

public class App {
    public static void main(String[] args) {
        System.out.println("=== FinCore Engine (BigDecimal Precision) ===");

        LedgerService service = new LedgerService();
        String accountId = "ACC-001";

        service.deposit(accountId, new BigDecimal("1000.00"));
        service.withdraw(accountId, new BigDecimal("450.00"));

        System.out.println("Verified balance: " + service.getBalance(accountId));
    }
}