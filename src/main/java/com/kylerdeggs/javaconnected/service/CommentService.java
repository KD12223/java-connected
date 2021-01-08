package com.kylerdeggs.javaconnected.service;

import com.kylerdeggs.javaconnected.domain.Comment;
import com.kylerdeggs.javaconnected.domain.Post;
import com.kylerdeggs.javaconnected.domain.User;
import com.kylerdeggs.javaconnected.repository.CommentRepository;
import com.kylerdeggs.javaconnected.web.CommentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Provides methods for retrieving, creating, updating, and deleting a comment.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
@Service
public class CommentService {
    private final static Logger LOGGER = LoggerFactory.getLogger(CommentService.class);

    private final RabbitTemplate rabbitTemplate;
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;

    @Value(value = "${amqp.exchange.name}")
    private String exchangeName;

    @Value(value = "${amqp.queue.comment-name}")
    private String commentQueueName;

    @Value(value = "${amqp.queue.comment-delete-name}")
    private String commentDeletionQueueName;

    @Autowired
    public CommentService(RabbitTemplate rabbitTemplate, CommentRepository commentRepository,
                          PostService postService, UserService userService) {
        this.rabbitTemplate = rabbitTemplate;
        this.commentRepository = commentRepository;
        this.postService = postService;
        this.userService = userService;
    }

    /**
     * Retrieves all comments.
     *
     * @return A list of all comments
     */
    public List<Comment> allComments() {
        return commentRepository.findByPublishedTrue();
    }

    /**
     * Retrieves all comments by a specific user.
     *
     * @param authorId ID of the target user
     * @return A list of comments by the target user
     */
    public List<Comment> allCommentsByUser(String authorId) {
        User author = userService.verifyUser(authorId);

        return commentRepository.findAllByAuthorAndPublishedTrue(author);
    }

    /**
     * Finds a comment with the specified ID or throws an exception if none is found.
     *
     * @param commentId ID of the target user
     * @return The found comment
     * @throws NoSuchElementException A comment with the specified ID was not found
     */
    public Comment verifyComment(long commentId) {
        return findComment(commentId).orElseThrow(() ->
                new NoSuchElementException("A comment with ID " + commentId + " does not exist"));
    }

    /**
     * Determines if a comment with the specified ID exists.
     *
     * @param commentId ID of the target comment
     * @return True if a comment is found
     */
    public boolean commentExists(long commentId) {
        return commentRepository.findByIdAndPublishedTrue(commentId).isPresent();
    }

    /**
     * Processes a comment creation request by sending the comment to the correct RabbitMQ queue.
     *
     * @param commentDto Comment to be created
     */
    public void processComment(CommentDto commentDto) {
        if (userService.userExists(commentDto.getAuthorId()) && postService.postExists(commentDto.getPostId())) {
            LOGGER.info("A new comment is being sent to the exchange " + exchangeName
                    + " to be routed to the queue " + commentQueueName);
            rabbitTemplate.convertAndSend(commentQueueName, commentDto);
        } else
            throw new NoSuchElementException("A user or post with ID " +
                    commentDto.getAuthorId() + "/" + commentDto.getPostId() + " does not exist");
    }

    /**
     * Processes a comment deletion request by sending the comment ID to the correct RabbitMQ queue.
     *
     * @param commentId ID of the target comment
     */
    public void processCommentDeletion(long commentId) {
        if (commentExists(commentId)) {
            LOGGER.info("A comment deletion is being sent to exchange " + exchangeName
                    + " to be routed to the queue " + commentDeletionQueueName);
            rabbitTemplate.convertAndSend(commentDeletionQueueName, commentId);
        } else
            throw new NoSuchElementException("A comment with ID " + commentId + " does not exist");
    }

    /**
     * Helper method that searches for a specified comment.
     *
     * @param commentId ID of the target comment
     * @return An optional Comment object
     */
    private Optional<Comment> findComment(long commentId) {
        return commentRepository.findByIdAndPublishedTrue(commentId);
    }

    /**
     * Creates new comments by consuming the comment creation queue.
     *
     * @param commentDto Comment to be created
     */
    @RabbitListener(queues = "${amqp.queue.comment-name}")
    private void commentCreator(CommentDto commentDto) {
        User author = userService.verifyUser(commentDto.getAuthorId());
        Post post = postService.verifyPost(commentDto.getPostId());
        Comment comment = new Comment(post, author, commentDto.getCaption(), true, LocalDateTime.now());

        commentRepository.save(comment);
        LOGGER.info("A new comment with ID " + comment.getId() + " has been created");
    }

    /**
     * Deletes comments by consuming the comment deletion queue.
     *
     * @param commentId Comment to be deleted
     */
    @RabbitListener(queues = "${amqp.queue.comment-delete-name}")
    private void commentDeleter(long commentId) {
        Comment targetComment = verifyComment(commentId);

        targetComment.setPublished(false);
        targetComment.setDeletedAt(LocalDateTime.now());

        commentRepository.save(targetComment);
        LOGGER.info("Comment " + commentId + " has been unpublished");
    }
}
