package br.com.compass.services;

import br.com.compass.models.Account;

import java.math.BigDecimal;

public class TransactionService {

    public void deposit(Account account, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }
        account.setBalance(account.getBalance().add(BigDecimal.valueOf(amount)));
        account.addTransaction("Deposited: " + amount);
    }

    public void withdraw(Account account, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }
        if (account.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
        account.setBalance(account.getBalance().subtract(BigDecimal.valueOf(amount)));
        account.addTransaction("Withdrew: " + amount);
    }

    public void transfer(Account sourceAccount, Account targetAccount, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }
        if (sourceAccount.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(BigDecimal.valueOf(amount)));
        targetAccount.setBalance(targetAccount.getBalance().add(BigDecimal.valueOf(amount)));
        sourceAccount.addTransaction("Transferred " + amount + " to Account: " + targetAccount.getAccountNumber());
        targetAccount.addTransaction("Received " + amount + " from Account: " + sourceAccount.getAccountNumber());
    }
}
