package com.nms.controllers;

import com.nms.entities.User;
import com.nms.repositories.UserRepositories;
import com.nms.services.UserService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserRepositories userRepositories;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<User> allUsers = userService.getAllUsers();
            return new ResponseEntity<>(allUsers, HttpStatus.ACCEPTED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Unable to fetch users", HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{username}")
    public ResponseEntity<User> getParticularUser(@PathVariable String username) {
        try {
            Optional<User> byUsername = userRepositories.findByUsername(username);
            if (byUsername.isPresent()) {
                return new ResponseEntity<>(byUsername.get(), HttpStatus.ACCEPTED);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> saveUser(@RequestBody User newUser) {
        try {
            Optional<User> byUsername = userRepositories.findByUsername(newUser.getUsername());
            if (byUsername.isEmpty()) {
                User user = userService.saveUser(newUser);
                return new ResponseEntity<>(user, HttpStatus.CREATED);
            } else
                return new ResponseEntity<>("user already present in db :", HttpStatus.BAD_REQUEST);

        } catch (RuntimeException e) {
            return new ResponseEntity<>("Unable to create user", HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{username}")
    public ResponseEntity<?> removeUser(@PathVariable String username) {
        try {
            Optional<ObjectId> opid = userService.removeByName(username);
            if (opid.isPresent()) {
                return new ResponseEntity<>(opid.get(), HttpStatus.ACCEPTED);
            } else
                return new ResponseEntity<>("user not present in db", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to delete user", HttpStatus.BAD_REQUEST);
        }
    }

}
