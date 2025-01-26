package br.com.compass.repositories;

import br.com.compass.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private List<User> users = new ArrayList<>();

    public void save(User user) {
        users.add(user);
    }

    public Optional<User> findByCpf(String cpf) {
        return users.stream()
                .filter(user -> user.getCpf().equals(cpf))
                .findFirst();
    }
}
