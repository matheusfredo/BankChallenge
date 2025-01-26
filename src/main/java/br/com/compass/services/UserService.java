package br.com.compass.services;

import br.com.compass.models.User;
import br.com.compass.repositories.UserRepository;

import java.util.Optional;

public class UserService {
    private static UserRepository userRepository = UserRepository.getInstance();

    public Optional<User> getUserByCpf(String cpf) {
        return userRepository.findByCpf(cpf);
    }

    public boolean registerUser(User user) {
        if (isCpfRegistered(user.getCpf())) {
            return false; 
        }

        userRepository.save(user);
        return true; 
    }

    public boolean isCpfRegistered(String cpf) {
        return userRepository.findByCpf(cpf).isPresent(); 
    }

    public static UserRepository getUserRepository() {
        return userRepository;
    }

    public boolean isPasswordValid(String cpf, String password) {
        return userRepository.findByCpf(cpf)
                .map(user -> password != null && password.equals(user.getPassword()))
                .orElse(false);
    }
}
