package br.com.compass.services;

import br.com.compass.models.Account;
import br.com.compass.repositories.AccountRepository;
import br.com.compass.repositories.TransactionRepository;
import br.com.compass.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }
    
    public List<String> getTransactionHistory(Long accountNumber) {
        return transactionRepository.getTransactionHistory(accountNumber);
    }


    public void deposit(Account account, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }
        account.setBalance(account.getBalance().add(BigDecimal.valueOf(amount)));

        transactionRepository.saveTransaction(
            LocalDateTime.now(),
            amount,
            "Deposit",
            account.getAccountNumber(),
            null
        );

        accountRepository.updateAccountBalance(account);
    }

    public void withdraw(Account account, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }
        if (account.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
        account.setBalance(account.getBalance().subtract(BigDecimal.valueOf(amount)));

        transactionRepository.saveTransaction(
            LocalDateTime.now(),
            amount,
            "Withdrawal",
            account.getAccountNumber(),
            null
        );

        accountRepository.updateAccountBalance(account);
    }

    public void transfer(Account sourceAccount, Account targetAccount, double amount, String targetUserName) {
        if (targetAccount == null) {
            throw new IllegalArgumentException("Target account does not exist.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }
        if (sourceAccount.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(BigDecimal.valueOf(amount)));
        targetAccount.setBalance(targetAccount.getBalance().add(BigDecimal.valueOf(amount)));

        LocalDateTime timestamp = LocalDateTime.now();
        transactionRepository.saveTransaction(
            timestamp,
            amount,
            String.format("Transferred %.2f from %d to %d | %s", amount, sourceAccount.getAccountNumber(), targetAccount.getAccountNumber(), targetUserName),
            sourceAccount.getAccountNumber(),
            targetAccount.getAccountNumber()
        );

        accountRepository.updateAccountBalance(sourceAccount);
        accountRepository.updateAccountBalance(targetAccount);
    }

    public boolean isPasswordValid(String cpf, String password) {
        return userRepository.findByCpf(cpf)
                .map(user -> password != null && password.equals(user.getPassword()))
                .orElse(false);
    }
}
