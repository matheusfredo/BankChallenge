package br.com.compass.services;

import br.com.compass.models.User;
import br.com.compass.repositories.UserRepository;

import java.util.Optional;

public class UserService {
    private static UserRepository userRepository = new UserRepository();

    public boolean registerUser(User user) {
        if (isCpfRegistered(user.getCpf())) {
            return false; 
        }

        userRepository.save(user);
        return true; 
    }

    public boolean isCpfRegistered(String cpf) {
        Optional<User> existingUser = userRepository.findByCpf(cpf);
        return existingUser.isPresent(); 
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
