package com.kylerdeggs.javaconnected.service;

import com.kylerdeggs.javaconnected.domain.Comment;
import com.kylerdeggs.javaconnected.domain.Post;
import com.kylerdeggs.javaconnected.domain.User;
import com.kylerdeggs.javaconnected.repository.CommentRepository;
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
 * Unit tests for the CommentService class.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository mockCommentRepository;

    @Mock
    private UserService mockUserService;

    @InjectMocks
    private CommentService commentService;

    private final List<User> userList = new ArrayList<>();

    private final List<Post> postList = new ArrayList<>();

    private final List<Comment> commentList = new ArrayList<>();

    @Before
    public void setUp() {
        LocalDateTime currentTime = LocalDateTime.now();
        User user1 = new User();
        User user2 = new User();
        Post post1 = new Post();
        Post post2 = new Post();
        Comment comment1 = new Comment(post1, user1, "My first comment", true, currentTime);
        Comment comment2 = new Comment(post2, user1, "My second comment", true, currentTime);
        Comment comment3 = new Comment(post2, user2, "Great post!", true, currentTime);

        userList.addAll(Arrays.asList(user1, user2));
        postList.addAll(Arrays.asList(post1, post2));
        commentList.addAll(Arrays.asList(comment1, comment2, comment3));
        when(mockCommentRepository.findByIdAndPublishedTrue(3L)).thenReturn(Optional.ofNullable(commentList.get(2)));
    }

    @Test
    public void allComments() {
        when(mockCommentRepository.findByPublishedTrue()).thenReturn(commentList);
        List<Comment> foundComments = commentService.allComments();

        assertEquals(3, foundComments.size());
        assertEquals(commentList.get(0), foundComments.get(0));
        assertEquals(commentList.get(1), foundComments.get(1));
        assertEquals(commentList.get(2), foundComments.get(2));
    }

    @Test
    public void allCommentsByUser() {
        User targetUser = userList.get(0);
        when(mockUserService.verifyUser("abc123")).thenReturn(targetUser);
        when(mockCommentRepository.findAllByAuthorAndPublishedTrue(targetUser)).thenReturn(
                commentList.stream().filter(comment -> comment.getAuthor().equals(targetUser))
                        .collect(Collectors.toList())
        );
        List<Comment> foundComments = commentService.allCommentsByUser("abc123");

        assertEquals(2, foundComments.size());
        assertEquals(commentList.get(0), foundComments.get(0));
        assertEquals(commentList.get(1), foundComments.get(1));
    }

    @Test
    public void allCommentsForPost() {
        Post targetPost = postList.get(0);
        when(mockCommentRepository.findAllByPostAndPublishedTrue(targetPost)).thenReturn(
                commentList.stream().filter(comment -> comment.getPost().equals(targetPost))
                        .collect(Collectors.toList())
        );
        List<Comment> foundComments = commentService.allCommentsForPost(targetPost);

        assertEquals(1, foundComments.size());
        assertEquals(commentList.get(0), foundComments.get(0));
    }

    @Test
    public void verifyComment() {
        assertEquals(commentList.get(2), commentService.verifyComment(3));
    }

    @Test
    public void commentExists() {
        assertTrue(commentService.commentExists(3));
        assertFalse(commentService.commentExists(4));
    }
}
