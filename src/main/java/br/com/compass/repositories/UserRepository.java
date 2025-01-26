package br.com.compass.repositories;

import br.com.compass.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private static UserRepository instance; 
    private List<User> users = new ArrayList<>();

    private UserRepository() {
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public void save(User user) {
        users.add(user);
    }

    public Optional<User> findByCpf(String cpf) {
        return users.stream()
                .filter(user -> user.getCpf().equals(cpf))
                .findFirst();
    }
}
