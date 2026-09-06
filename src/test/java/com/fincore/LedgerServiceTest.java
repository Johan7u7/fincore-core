package com.fincore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LedgerService Domain & Balance Tests")
class LedgerServiceTest {

    private LedgerService ledgerService;
    private static final String ACCOUNT_ID = "ACC-TEST-001";

    @BeforeEach
    void setUp() {
        ledgerService = new LedgerService();
    }

    @Test
    @DisplayName("Should correctly calculate balance after successive deposits")
    void testDepositCalculatesBalanceCorrectly() {
        ledgerService.deposit(ACCOUNT_ID, 500.0);
        ledgerService.deposit(ACCOUNT_ID, 250.50);

        assertEquals(750.50, ledgerService.getBalance(ACCOUNT_ID), 0.001);
        assertEquals(2, ledgerService.getHistory(ACCOUNT_ID).size());
    }

    @Test
    @DisplayName("Should deduct balance correctly on valid withdrawal")
    void testWithdrawalDeductsBalance() {
        ledgerService.deposit(ACCOUNT_ID, 1000.0);
        Transaction tx = ledgerService.withdraw(ACCOUNT_ID, 400.0);

        assertNotNull(tx);
        assertEquals(TransactionType.WITHDRAWAL, tx.getType());
        assertEquals(600.0, ledgerService.getBalance(ACCOUNT_ID), 0.001);
    }

    @Test
    @DisplayName("Should throw InsufficientFundsException and protect ledger on overdraft")
    void testOverdraftBlockedAndLedgerUntouched() {
        ledgerService.deposit(ACCOUNT_ID, 100.0);

        InsufficientFundsException exception = assertThrows(
                InsufficientFundsException.class,
                () -> ledgerService.withdraw(ACCOUNT_ID, 150.0));

        assertTrue(exception.getMessage().contains("Overdraft blocked"));
        // El balance y el historial deben permanecer intactos
        assertEquals(100.0, ledgerService.getBalance(ACCOUNT_ID), 0.001);
        assertEquals(1, ledgerService.getHistory(ACCOUNT_ID).size());
    }

    @Test
    @DisplayName("Should enforce immutability on transaction history list")
    void testHistoryListIsImmutable() {
        ledgerService.deposit(ACCOUNT_ID, 200.0);
        List<Transaction> history = ledgerService.getHistory(ACCOUNT_ID);

        // Intentar alterar la lista directamente debe lanzar
        // UnsupportedOperationException
        assertThrows(UnsupportedOperationException.class, () -> {
            history.add(new Transaction(500.0, TransactionType.DEPOSIT));
        });
    }
}