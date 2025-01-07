package com.example.vinylstore.SERVICES;

import com.example.vinylstore.MODELS.User;
import com.example.vinylstore.REPOSITORIES.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder  passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String password) {
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already taken");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRoles("USER");

        return userRepository.save(newUser);
    }
    public User getUserByUsername(String username) {
        try{ User user =userRepository.findByUsername(username);
        return user;
        }
        catch (Exception e)
        {
            throw new RuntimeException("User not found");
        }
    }
}
