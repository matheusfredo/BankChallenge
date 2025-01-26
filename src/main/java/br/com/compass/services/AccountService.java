package br.com.compass.services;

import br.com.compass.models.Account;
import br.com.compass.repositories.AccountRepository;
import br.com.compass.repositories.UserRepository;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

public class AccountService {
    private static AtomicLong accountNumberGenerator = new AtomicLong(1000L); // Inicializa com 10 dígitos
    private AccountRepository accountRepository = AccountRepository.getInstance(); // Repositório único
    private UserRepository userRepository = UserService.getUserRepository();

    public Account getAccountByCpf(String cpf) {
        return accountRepository.findByUserCpf(cpf)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for CPF: " + cpf));
    }

    public Account openAccount(String userCpf, String accountType) {
        if (userRepository.findByCpf(userCpf).isEmpty()) {
            throw new IllegalArgumentException("User not found. Please register first.");
        }

        Long accountNumber = accountNumberGenerator.getAndIncrement(); // Gerar número único
        Account account = new Account(accountNumber, accountType, BigDecimal.ZERO, userCpf);
        accountRepository.save(account);

        return account;
    }
}
