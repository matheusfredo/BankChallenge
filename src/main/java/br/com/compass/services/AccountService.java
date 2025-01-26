package br.com.compass.services;

import br.com.compass.models.Account;
import br.com.compass.models.User;
import br.com.compass.repositories.AccountRepository;
import br.com.compass.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;

public class AccountService {
    private AccountRepository accountRepository = new AccountRepository();
    private UserRepository userRepository = UserService.getUserRepository();
    
    public Account openAccount(String cpf, String accountType) {
        Optional<User> userOptional = userRepository.findByCpf(cpf);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found. Please register first.");
        }
        
        User user = userOptional.get();
        Long userId = user.getId();
        
        Account account = new Account(null, accountType, BigDecimal.ZERO, userId);
        accountRepository.save(account);

        return account;
    }
}
