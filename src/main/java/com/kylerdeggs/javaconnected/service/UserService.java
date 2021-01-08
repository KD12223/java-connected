package com.kylerdeggs.javaconnected.service;

import com.kylerdeggs.javaconnected.domain.User;
import com.kylerdeggs.javaconnected.repository.UserRepository;
import com.kylerdeggs.javaconnected.web.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Provides methods for retrieving, creating, and updating users.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all users.
     *
     * @return A list of all users
     */
    public Iterable<User> allUsers() {
        return userRepository.findAll();
    }

    /**
     * Finds a user with the specified ID or throws an exception if none is found.
     *
     * @param userId ID of the target user
     * @return The found user
     * @throws NoSuchElementException A user with the specified ID was not found
     */
    public User verifyUser(String userId) throws NoSuchElementException {
        return findUser(userId).orElseThrow(() ->
                new NoSuchElementException("A user with ID: " + userId + " does not exist"));
    }

    /**
     * Determines if a user with the specified ID exists.
     *
     * @param userId ID of the target user
     * @return True if a user is found
     */
    public boolean userExists(String userId) {
        return findUser(userId).isPresent();
    }

    /**
     * Creates a new user with the specified information.
     *
     * @param user Information of the new user
     */
    public void createUser(UserDto user) {
        if (user.getFirstName() == null || user.getLastName() == null || user.getEmail() == null)
            throw new IllegalArgumentException("The request is missing required information");

        userRepository.save(new User(user.getId(), user.getFirstName(), user.getLastName(), user.getPhone(),
                user.getEmail(), LocalDateTime.now(), LocalDateTime.now()));
        LOGGER.info("A new user has been created");
    }

    /**
     * Updates a user with the specified new information
     *
     * @param userId      ID of the target user
     * @param updatedUser UserDto with updated information
     * @return The updated user
     */
    public User updateUser(String userId, UserDto updatedUser) {
        User original = verifyUser(userId);

        if (updatedUser.getFirstName() != null)
            original.setFirstName(updatedUser.getFirstName());
        if (updatedUser.getLastName() != null)
            original.setLastName(updatedUser.getLastName());
        if (updatedUser.getPhone() != null)
            original.setPhone(updatedUser.getPhone());
        if (updatedUser.getEmail() != null)
            original.setEmail(updatedUser.getEmail());

        LOGGER.info("Updating user details for user with ID: " + userId);
        return userRepository.save(original);
    }

    /**
     * Helper method that searches for a specified user.
     *
     * @param userId ID of the target user
     * @return An optional User object
     */
    private Optional<User> findUser(String userId) {
        return userRepository.findById(userId);
    }
}
