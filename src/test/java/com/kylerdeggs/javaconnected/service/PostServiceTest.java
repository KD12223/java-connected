package com.kylerdeggs.javaconnected.service;

import com.kylerdeggs.javaconnected.domain.Post;
import com.kylerdeggs.javaconnected.domain.User;
import com.kylerdeggs.javaconnected.repository.PostRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the PostService class.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class PostServiceTest {

    @Mock
    private PostRepository mockPostRepository;

    @Mock
    private UserService mockUserService;

    @InjectMocks
    private PostService postService;

    private final List<User> userList = new ArrayList<>();

    private final List<Post> postList = new ArrayList<>();

    @Before
    public void setUp() {
        LocalDateTime currentTime = LocalDateTime.now();
        User user1 = new User();
        User user2 = new User();
        Post post1 = new Post(user1, "My first post", false, null,
                "My first caption", true, currentTime);
        Post post2 = new Post(user1, "My second post", false, null,
                "My second caption", true, currentTime);
        Post post3 = new Post(user2, "Cool post", false, null,
                "My awesome caption", true, currentTime);

        userList.addAll(Arrays.asList(user1, user2));
        postList.addAll(Arrays.asList(post1, post2, post3));
        when(mockPostRepository.findByIdAndPublishedTrue(1L)).thenReturn(Optional.ofNullable(postList.get(0)));
    }

    @Test
    public void allPosts() {
        when(mockPostRepository.findByPublishedTrue()).thenReturn(postList);
        List<Post> foundPosts = postService.allPosts();

        assertEquals(3, foundPosts.size());
        assertEquals(postList.get(0), foundPosts.get(0));
        assertEquals(postList.get(1), foundPosts.get(1));
        assertEquals(postList.get(2), foundPosts.get(2));
    }

    @Test
    public void allPostsByUser() {
        User targetUser = userList.get(0);
        when(mockUserService.verifyUser("abc123")).thenReturn(targetUser);
        when(mockPostRepository.findAllByAuthorAndPublishedTrue(targetUser)).thenReturn(
                postList.stream().filter(post -> post.getAuthor().equals(targetUser)).collect(Collectors.toList())
        );
        List<Post> foundPosts = postService.allPostsByUser("abc123");

        assertEquals(2, foundPosts.size());
        assertEquals(postList.get(0), foundPosts.get(0));
        assertEquals(postList.get(1), foundPosts.get(1));
    }

    @Test
    public void verifyPost() {
        assertEquals(postList.get(0), postService.verifyPost(1));
    }

    @Test
    public void postExists() {
        assertTrue(postService.postExists(1));
        assertFalse(postService.postExists(2));
    }
}
