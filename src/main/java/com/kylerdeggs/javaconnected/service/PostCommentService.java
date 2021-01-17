package com.kylerdeggs.javaconnected.service;

import com.kylerdeggs.javaconnected.domain.Post;
import com.kylerdeggs.javaconnected.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods for retrieving a combination of posts and comments.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
@Service
public class PostCommentService {
    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public PostCommentService(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    /**
     * Retrieves all posts and corresponding comments for the post.
     *
     * @return A list of all posts and corresponding comments
     */
    public List<PostCommentDto> allPostsAndComments() {
        List<PostCommentDto> output = new ArrayList<>();

        postService.allPosts().forEach(post -> output.add(new PostCommentDto(post, retrieveCommentsForPost(post))));

        return output;
    }

    /**
     * Retrieves all posts by a specific user with the corresponding comments.
     *
     * @param authorId ID of the target user
     * @return A list of all posts by the target user with the corresponding comments
     */
    public List<PostCommentDto> allPostsByUserAndComments(String authorId) {
        List<PostCommentDto> output = new ArrayList<>();

        postService.allPostsByUser(authorId).forEach(post ->
                output.add(new PostCommentDto(post, retrieveCommentsForPost(post))));

        return output;
    }

    /**
     * Helper method that transforms comments of a post into a list of CommentData objects.
     *
     * @param post Target post
     * @return List of CommentData objects corresponding to the current post
     */
    private List<CommentData> retrieveCommentsForPost(Post post) {
        List<CommentData> comments = new ArrayList<>();

        commentService.allCommentsForPost(post).forEach(comment ->
                comments.add(new CommentData(comment.getId(), comment.getAuthor(), comment.getCaption(),
                        comment.getCreatedAt())));

        return comments;
    }

    /**
     * Post and comment combination object.
     *
     * @author Kyler Deggs
     * @version 1.0.0
     */
    public static class PostCommentDto {
        @NotNull
        private final Post post;

        private final @NotNull List<CommentData> comments;

        public PostCommentDto(Post post, List<CommentData> comments) {
            this.post = post;
            this.comments = comments;
        }

        public Post getPost() {
            return post;
        }

        public List<CommentData> getComments() {
            return comments;
        }
    }

    /**
     * Comment representation object used specifically for the PostComment service.
     *
     * @author Kyler Deggs
     * @version 1.0.0
     */
    private static class CommentData {
        private final long id;

        private final User author;

        private final String caption;

        private final LocalDateTime createdAt;

        public CommentData(long id, User author, String caption, LocalDateTime createdAt) {
            this.id = id;
            this.author = author;
            this.caption = caption;
            this.createdAt = createdAt;
        }

        public long getId() {
            return id;
        }

        public User getAuthor() {
            return author;
        }

        public String getCaption() {
            return caption;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }
}
