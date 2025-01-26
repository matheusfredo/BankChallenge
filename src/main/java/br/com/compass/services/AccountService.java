package br.com.compass.services;

import br.com.compass.models.Account;
import br.com.compass.repositories.AccountRepository;
import br.com.compass.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

public class AccountService {
    private static AtomicLong accountNumberGenerator = new AtomicLong(1000L);
    private AccountRepository accountRepository = AccountRepository.getInstance();
    private UserRepository userRepository = UserService.getUserRepository();

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

        Long accountNumber = accountNumberGenerator.getAndIncrement();
        Account account = new Account(accountNumber, accountType, BigDecimal.ZERO, userCpf);
        accountRepository.save(account);

        return account;
    }
}
