package br.com.compass.services;

import br.com.compass.models.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionService {

    public void deposit(Account account, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }
        account.setBalance(account.getBalance().add(BigDecimal.valueOf(amount)));

        String timestamp = getCurrentTimestamp();
        account.addTransaction(String.format(
            "Deposited %.2f - %s", amount, timestamp
        ));
    }

    public void withdraw(Account account, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }
        if (account.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
        account.setBalance(account.getBalance().subtract(BigDecimal.valueOf(amount)));

        String timestamp = getCurrentTimestamp();
        account.addTransaction(String.format(
            "Withdrew %.2f - %s", amount, timestamp
        ));
    }

    public void transfer(Account sourceAccount, Account targetAccount, double amount, String targetUserName) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }
        if (sourceAccount.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(BigDecimal.valueOf(amount)));
        targetAccount.setBalance(targetAccount.getBalance().add(BigDecimal.valueOf(amount)));

        String timestamp = getCurrentTimestamp();

        sourceAccount.addTransaction(String.format(
            "Transferred %.2f to Account: %d | %s - %s",
            amount, targetAccount.getAccountNumber(), targetUserName, timestamp
        ));
        targetAccount.addTransaction(String.format(
            "Received %.2f from Account: %d - %s",
            amount, sourceAccount.getAccountNumber(), timestamp
        ));
    }

    public boolean isPasswordValid(String cpf, String password) {
        UserService userService = new UserService();
        return userService.isPasswordValid(cpf, password);
    }

    private String getCurrentTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm");
        return LocalDateTime.now().format(formatter);
    }
}
