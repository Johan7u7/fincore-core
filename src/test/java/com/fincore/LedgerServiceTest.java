package com.fincore;

import com.fincore.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LedgerServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    private LedgerService ledgerService;

    @BeforeEach
    void setUp() {
        ledgerService = new LedgerService(transactionRepository);
    }

    @Test
    void testDepositPersistsTransaction() {
        String accountId = "ACC-001";
        BigDecimal amount = new BigDecimal("100.00");

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction tx = ledgerService.deposit(accountId, amount);

        assertNotNull(tx);
        assertEquals(accountId, tx.getAccountId());
        assertEquals(amount, tx.getAmount());
        assertEquals(TransactionType.DEPOSIT, tx.getType());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testWithdrawThrowsWhenInsufficientFunds() {
        String accountId = "ACC-001";
        when(transactionRepository.findByAccountIdOrderByTimestampAsc(accountId))
                .thenReturn(List.of());

        assertThrows(InsufficientFundsException.class, () -> {
            ledgerService.withdraw(accountId, new BigDecimal("50.00"));
        });
    }

    @Test
    void testCalculateBalance() {
        String accountId = "ACC-001";
        List<Transaction> transactions = List.of(
                new Transaction(accountId, new BigDecimal("100.00"), TransactionType.DEPOSIT),
                new Transaction(accountId, new BigDecimal("30.00"), TransactionType.WITHDRAWAL));

        when(transactionRepository.findByAccountIdOrderByTimestampAsc(accountId))
                .thenReturn(transactions);

        BigDecimal balance = ledgerService.getBalance(accountId);
        assertEquals(new BigDecimal("70.00"), balance);
    }
}