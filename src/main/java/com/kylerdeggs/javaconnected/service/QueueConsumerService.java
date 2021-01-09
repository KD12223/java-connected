package com.kylerdeggs.javaconnected.service;

import com.kylerdeggs.javaconnected.domain.Comment;
import com.kylerdeggs.javaconnected.domain.Post;
import com.kylerdeggs.javaconnected.domain.User;
import com.kylerdeggs.javaconnected.web.CommentDto;
import com.kylerdeggs.javaconnected.web.PostDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * RabbitMQ service that consumes all queues.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
@Service
public class QueueConsumerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueConsumerService.class);

    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public QueueConsumerService(PostService postService, CommentService commentService, UserService userService) {
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
    }

    /**
     * Creates new posts by consuming the post creation queue.
     *
     * @param postDto Post to be created
     */
    @RabbitListener(queues = "${amqp.queue.post-name}")
    private void postCreator(PostDto postDto) {
        User author = userService.verifyUser(postDto.getAuthorId());
        String mediaLocation = postDto.getMediaLocation();
        Post post = new Post(author, postDto.getTitle(), mediaLocation != null,
                mediaLocation, postDto.getCaption(), true, LocalDateTime.now());

        postService.savePost(post);
        LOGGER.info("A new post with ID " + post.getId() + " has been created");
    }

    /**
     * Modifies the like count of post by consuming the like queue.
     *
     * @param likeDto Like that contains information on whether to add or remove a like
     */
    @RabbitListener(queues = "${amqp.queue.like-name}")
    private void likeModifier(PostService.LikeDto likeDto) {
        Post targetPost = postService.verifyPost(likeDto.getPostId());

        if (likeDto.isAddLike())
            targetPost.setLikeCount(targetPost.getLikeCount() + 1);
        else
            targetPost.setLikeCount(targetPost.getLikeCount() != 0 ? targetPost.getLikeCount() - 1 : 0);

        postService.savePost(targetPost);
        LOGGER.info("A like has been " + (likeDto.isAddLike() ? "added" : "removed")
                + " to post " + targetPost.getId());
    }

    /**
     * Deletes posts by consuming the post deletion queue.
     *
     * @param postId Post to be deleted
     */
    @RabbitListener(queues = "${amqp.queue.post-delete-name}")
    private void postDeleter(long postId) {
        Post targetPost = postService.verifyPost(postId);

        if (targetPost.getHasMedia()) {
            postService.deleteMedia(targetPost.getMediaLocation());
            targetPost.setHasMedia(false);
            targetPost.setMediaLocation(null);
        }
        targetPost.setPublished(false);
        targetPost.setDeletedAt(LocalDateTime.now());
        commentService.allCommentsForPost(targetPost)
                .forEach(comment -> commentService.processCommentDeletion(comment.getId()));

        postService.savePost(targetPost);
        LOGGER.info("Post " + postId + " has been unpublished");
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

        commentService.saveComment(comment);
        LOGGER.info("A new comment with ID " + comment.getId() + " has been created");
    }

    /**
     * Deletes comments by consuming the comment deletion queue.
     *
     * @param commentId Comment to be deleted
     */
    @RabbitListener(queues = "${amqp.queue.comment-delete-name}")
    private void commentDeleter(long commentId) {
        Comment targetComment = commentService.verifyComment(commentId);

        targetComment.setPublished(false);
        targetComment.setDeletedAt(LocalDateTime.now());

        commentService.saveComment(targetComment);
        LOGGER.info("Comment " + commentId + " has been unpublished");
    }
}
