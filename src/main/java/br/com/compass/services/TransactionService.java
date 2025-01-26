package br.com.compass.services;

import br.com.compass.models.Account;
import br.com.compass.models.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionService {

    public Transaction deposit(Account account, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        account.setBalance(account.getBalance().add(amount));
        return new Transaction(null, "Deposit", amount, LocalDateTime.now(), null, account.getId());
    }

    public Transaction withdraw(Account account, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        account.setBalance(account.getBalance().subtract(amount));
        return new Transaction(null, "Withdrawal", amount, LocalDateTime.now(), account.getId(), null);
    }

    public Transaction transfer(Account sourceAccount, Account destinationAccount, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        return new Transaction(null, "Transfer", amount, LocalDateTime.now(), sourceAccount.getId(), destinationAccount.getId());
    }
}
