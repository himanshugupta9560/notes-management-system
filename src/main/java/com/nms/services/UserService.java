package com.nms.services;

import com.nms.entities.User;
import com.nms.repositories.UserRepositories;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepositories userRepositories;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepositories.findAll();
    }

    public User saveUser(User newUser) {
        newUser.setDate(new Date());
        return userRepositories.save(newUser);
    }

    public User registerUser(User user) {
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user.setDate(new Date());
        return userRepositories.save(user);
    }

    public Optional<ObjectId> removeByName(String username) {
        Optional<User> byUsername = userRepositories.findByUsername(username);
        if (byUsername.isPresent()) {
            userRepositories.delete(byUsername.get());
            return Optional.ofNullable(byUsername.get().getId());
        }
        return Optional.empty();
    }

}
