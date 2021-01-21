package com.kylerdeggs.javaconnected.web.controllers;

import com.kylerdeggs.javaconnected.domain.User;
import com.kylerdeggs.javaconnected.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to handle all requests pertaining to a user.
 *
 * @author Kyler Deggs
 * @version 1.3.1
 */
@RestController
@RequestMapping("v1/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Iterable<User> getAllUsers() {
        return userService.allUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") String userId) {
        return userService.verifyUser(userId);
    }
}
