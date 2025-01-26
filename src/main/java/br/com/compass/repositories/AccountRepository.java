package br.com.compass.repositories;

import br.com.compass.models.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountRepository {
    private static AccountRepository instance;
    private List<Account> accounts = new ArrayList<>();

    private AccountRepository() {
    }

    public static AccountRepository getInstance() {
        if (instance == null) {
            instance = new AccountRepository();
        }
        return instance;
    }

    public void save(Account account) {
        accounts.add(account);
    }

    public Optional<Account> findByUserCpf(String cpf) {
        return accounts.stream()
                .filter(account -> account.getUserCpf().equals(cpf))
                .findFirst();
    }
    public Optional<Account> findByAccountNumber(Long accountNumber) {
        return accounts.stream()
                .filter(account -> account.getAccountNumber().equals(accountNumber))
                .findFirst();
    }

}
