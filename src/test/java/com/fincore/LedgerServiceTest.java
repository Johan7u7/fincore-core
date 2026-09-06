package com.fincore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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
    @DisplayName("Should correctly calculate balance after successive deposits with high precision")
    void testDepositCalculatesBalanceCorrectly() {
        ledgerService.deposit(ACCOUNT_ID, new BigDecimal("500.00"));
        ledgerService.deposit(ACCOUNT_ID, new BigDecimal("250.55"));

        assertEquals(new BigDecimal("750.55"), ledgerService.getBalance(ACCOUNT_ID));
        assertEquals(2, ledgerService.getHistory(ACCOUNT_ID).size());
    }

    @Test
    @DisplayName("Should deduct balance correctly on valid withdrawal")
    void testWithdrawalDeductsBalance() {
        ledgerService.deposit(ACCOUNT_ID, new BigDecimal("1000.00"));
        Transaction tx = ledgerService.withdraw(ACCOUNT_ID, new BigDecimal("400.00"));

        assertNotNull(tx);
        assertEquals(TransactionType.WITHDRAWAL, tx.getType());
        assertEquals(new BigDecimal("600.00"), ledgerService.getBalance(ACCOUNT_ID));
    }

    @Test
    @DisplayName("Should throw InsufficientFundsException and protect ledger on overdraft")
    void testOverdraftBlockedAndLedgerUntouched() {
        ledgerService.deposit(ACCOUNT_ID, new BigDecimal("100.00"));

        InsufficientFundsException exception = assertThrows(
                InsufficientFundsException.class,
                () -> ledgerService.withdraw(ACCOUNT_ID, new BigDecimal("150.00")));

        assertTrue(exception.getMessage().contains("Overdraft blocked"));
        assertEquals(new BigDecimal("100.00"), ledgerService.getBalance(ACCOUNT_ID));
        assertEquals(1, ledgerService.getHistory(ACCOUNT_ID).size());
    }

    @Test
    @DisplayName("Should enforce immutability on transaction history list")
    void testHistoryListIsImmutable() {
        ledgerService.deposit(ACCOUNT_ID, new BigDecimal("200.00"));
        List<Transaction> history = ledgerService.getHistory(ACCOUNT_ID);

        assertThrows(UnsupportedOperationException.class, () -> {
            history.add(new Transaction(new BigDecimal("500.00"), TransactionType.DEPOSIT));
        });
    }

    @Test
    @DisplayName("Should transfer funds atomically between two distinct accounts")
    void testAtomicTransferSuccess() {
        String destAccount = "ACC-TEST-002";
        ledgerService.deposit(ACCOUNT_ID, new BigDecimal("1000.00"));

        ledgerService.transfer(ACCOUNT_ID, destAccount, new BigDecimal("400.00"));

        assertEquals(new BigDecimal("600.00"), ledgerService.getBalance(ACCOUNT_ID));
        assertEquals(new BigDecimal("400.00"), ledgerService.getBalance(destAccount));
        assertEquals(2, ledgerService.getHistory(ACCOUNT_ID).size());
        assertEquals(1, ledgerService.getHistory(destAccount).size());
    }

    @Test
    @DisplayName("Should prevent transfer when source account has insufficient funds")
    void testTransferInsufficientFundsBlocked() {
        String destAccount = "ACC-TEST-002";
        ledgerService.deposit(ACCOUNT_ID, new BigDecimal("100.00"));

        assertThrows(InsufficientFundsException.class, () -> {
            ledgerService.transfer(ACCOUNT_ID, destAccount, new BigDecimal("300.00"));
        });

        // Ambas cuentas deben mantenerse intactas
        assertEquals(new BigDecimal("100.00"), ledgerService.getBalance(ACCOUNT_ID));
        assertEquals(BigDecimal.ZERO, ledgerService.getBalance(destAccount));
        assertEquals(1, ledgerService.getHistory(ACCOUNT_ID).size());
        assertEquals(0, ledgerService.getHistory(destAccount).size());
    }

    @Test
    @DisplayName("Should reject transfer when source and destination are the same account")
    void testTransferSameAccountBlocked() {
        ledgerService.deposit(ACCOUNT_ID, new BigDecimal("500.00"));

        assertThrows(IllegalArgumentException.class, () -> {
            ledgerService.transfer(ACCOUNT_ID, ACCOUNT_ID, new BigDecimal("100.00"));
        });

        assertEquals(new BigDecimal("500.00"), ledgerService.getBalance(ACCOUNT_ID));
    }
}