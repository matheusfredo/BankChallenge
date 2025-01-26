package br.com.compass.services;

import br.com.compass.models.User;
import br.com.compass.repositories.UserRepository;

public class UserService {
    private static UserRepository userRepository = new UserRepository();

    public boolean registerUser(User user) {
        if (userRepository.findByCpf(user.getCpf()).isPresent()) {
            return false; 
        }

        userRepository.save(user);
        return true; 
    }

    public static UserRepository getUserRepository() {
        return userRepository;
    }
}
