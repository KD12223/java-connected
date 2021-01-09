package com.kylerdeggs.javaconnected.web;

import com.kylerdeggs.javaconnected.domain.User;
import com.kylerdeggs.javaconnected.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * Controller to handle all requests for User objects.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
@RestController
@RequestMapping(path = "v1/api/users")
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

    @GetMapping(path = "/{id}")
    public User getUser(@PathVariable(value = "id") String userId) {
        return userService.verifyUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody @Validated UserDto user) {
        userService.createUser(user);
    }

    @PatchMapping(path = "/{id}")
    public User updateUser(@PathVariable(value = "id") String userId, @RequestBody @Validated UserDto user) {
        return userService.updateUser(userId, user);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public String return404(NoSuchElementException exception) {
        return exception.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String return400(IllegalArgumentException exception) {
        return exception.getMessage();
    }
}
