package com.fincore;

import com.fincore.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class LedgerService {

    private final TransactionRepository transactionRepository;

    public LedgerService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction deposit(String accountId, BigDecimal amount) {
        Transaction tx = new Transaction(accountId, amount, TransactionType.DEPOSIT);
        return transactionRepository.save(tx);
    }

    @Transactional
    public Transaction withdraw(String accountId, BigDecimal amount) {
        BigDecimal currentBalance = getBalance(accountId);
        if (currentBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    String.format("Overdraft blocked: Current balance is %s, attempted withdrawal of %s",
                            currentBalance.toPlainString(), amount.toPlainString()));
        }

        Transaction tx = new Transaction(accountId, amount, TransactionType.WITHDRAWAL);
        return transactionRepository.save(tx);
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(String accountId) {
        List<Transaction> history = transactionRepository.findByAccountIdOrderByTimestampAsc(accountId);

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

    @Transactional(readOnly = true)
    public List<Transaction> getHistory(String accountId) {
        List<Transaction> history = transactionRepository.findByAccountIdOrderByTimestampAsc(accountId);
        return Collections.unmodifiableList(history);
    }

    @Transactional
    public void transfer(String sourceAccountId, String destinationAccountId, BigDecimal amount) {
        if (sourceAccountId == null || destinationAccountId == null) {
            throw new IllegalArgumentException("Account IDs cannot be null.");
        }
        if (sourceAccountId.equals(destinationAccountId)) {
            throw new IllegalArgumentException("Cannot transfer funds to the same account.");
        }

        withdraw(sourceAccountId, amount);
        deposit(destinationAccountId, amount);
    }
}