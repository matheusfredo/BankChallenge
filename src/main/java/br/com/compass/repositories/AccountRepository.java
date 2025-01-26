package br.com.compass.repositories;

import br.com.compass.models.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountRepository {
    private List<Account> accounts = new ArrayList<>();

    public void save(Account account) {
        account.setId((long) (accounts.size() + 1)); 
        accounts.add(account);
    }

    public Optional<Account> findById(Long id) {
        return accounts.stream().filter(account -> account.getId().equals(id)).findFirst();
    }

    public List<Account> findAll() {
        return accounts;
    }
}
