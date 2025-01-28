package br.com.compass.services;

import br.com.compass.models.Account;
import br.com.compass.repositories.AccountRepository;
import br.com.compass.repositories.UserRepository;

import java.math.BigDecimal;

public class AccountService {
    private AccountRepository accountRepository;
    private UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public Account getAccountByCpf(String cpf) {
        return accountRepository.findByUserCpf(cpf)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for CPF: " + cpf));
    }

    public Account getAccountByNumber(String accountNumber) {
        try {
            Long accountNum = Long.parseLong(accountNumber);
            return accountRepository.findByAccountNumber(accountNum)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found for account number: " + accountNumber));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid account number format.");
        }
    }

    public Account openAccount(String userCpf, String accountType) {
        if (userRepository.findByCpf(userCpf).isEmpty()) {
            throw new IllegalArgumentException("User not found. Please register first.");
        }

        Long accountNumber = System.currentTimeMillis(); 
        Account account = new Account(accountNumber, accountType, BigDecimal.ZERO, userCpf);

        accountRepository.save(account);

        return account;
    }
}
