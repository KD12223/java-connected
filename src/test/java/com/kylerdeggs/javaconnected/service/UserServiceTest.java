package com.kylerdeggs.javaconnected.service;

import com.kylerdeggs.javaconnected.domain.User;
import com.kylerdeggs.javaconnected.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the UserService class.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository mockUserRepository;

    @InjectMocks
    private UserService userService;

    private final List<User> userList = new ArrayList<>();

    @Before
    public void setUp() {
        LocalDateTime currentTime = LocalDateTime.now();
        User user1 = new User("abc123", "John", "Doe", "1234567890",
                "johndoe@gmail.com", currentTime, currentTime);
        User user2 = new User("xyz321", "Jane", "Doe", "6986786577",
                "janedoe@gmail.com", currentTime, currentTime);

        userList.addAll(Arrays.asList(user1, user2));
    }

    @Test
    public void allUsers() {
        when(mockUserRepository.findAll()).thenReturn(userList);
        Iterable<User> foundUsers = userService.allUsers();
        User nextUser = foundUsers.iterator().next();

        assertEquals(3, ((Collection<User>) foundUsers).size());
        assertEquals(userList.get(0), nextUser);
        assertNotEquals(userList.get(1), nextUser);
    }

    @Test
    public void verifyUser() {
        when(mockUserRepository.findById("xyz321")).thenReturn(Optional.ofNullable(userList.get(1)));
        User foundUser = userService.verifyUser("xyz321");

        assertEquals(userList.get(1), foundUser);
        assertNotEquals(userList.get(0), foundUser);
    }
}
