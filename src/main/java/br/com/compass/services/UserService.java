package br.com.compass.services;

import br.com.compass.models.User;
import br.com.compass.repositories.UserRepository;

import java.util.Optional;

public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserByCpf(String cpf) {
        return userRepository.findByCpf(cpf);
    }

    public boolean registerUser(User user) {
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean isCpfRegistered(String cpf) {
        return userRepository.findByCpf(cpf).isPresent();
    }
    
    public boolean updateUserPassword(String cpf, String newPassword) {
        Optional<User> optionalUser = userRepository.findByCpf(cpf);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword(newPassword); 
            userRepository.update(user);  
            return true;
        }
        return false; 
    }


    public boolean isPasswordValid(String cpf, String password) {
        return userRepository.findByCpf(cpf)
                .map(user -> password != null && password.equals(user.getPassword()))
                .orElse(false);
    }
}
