package br.com.compass.services;

import br.com.compass.models.Account;
import br.com.compass.repositories.AccountRepository;
import br.com.compass.repositories.UserRepository;

import java.math.BigDecimal;

public class AccountService {
    private AccountRepository accountRepository = new AccountRepository();
    private UserRepository userRepository = UserService.getUserRepository(); // Compartilhar o repositório

    public Account openAccount(Long userId, String accountType) {
        // Verificar se o usuário existe
        if (userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("User not found. Please register first.");
        }

        // Criar a conta com saldo inicial 0
        Account account = new Account(null, accountType, BigDecimal.ZERO, userId);
        accountRepository.save(account);

        return account;
    }
}
