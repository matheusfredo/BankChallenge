package br.com.compass.repositories;

import br.com.compass.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private List<User> users = new ArrayList<>();

    public void save(User user) {
        user.setId((long) (users.size() + 1));  // Gera um ID único
        users.add(user);
    }

    public Optional<User> findById(Long id) {
        return users.stream().filter(user -> user.getId().equals(id)).findFirst();
    }

    public Optional<User> findByCpf(String cpf) {
        return users.stream().filter(user -> user.getCpf().equals(cpf)).findFirst();
    }

    public List<User> findAll() {
        return users;
    }
}
