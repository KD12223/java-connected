package com.kylerdeggs.javaconnected.service;

import com.kylerdeggs.javaconnected.configuration.AWSConfig;
import com.kylerdeggs.javaconnected.domain.Post;
import com.kylerdeggs.javaconnected.domain.User;
import com.kylerdeggs.javaconnected.repository.PostRepository;
import com.kylerdeggs.javaconnected.web.PostDto;
import org.apache.tika.mime.MimeTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Provides methods for retrieving, creating, updating, and deleting a post.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
@Service
public class PostService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostService.class);

    private final RabbitTemplate rabbitTemplate;
    private final AWSConfig aws;
    private final PostRepository postRepository;
    private final UserService userService;

    @Value("${amqp.exchange.name}")
    private String exchangeName;

    @Value("${amqp.queue.post-name}")
    private String postQueueName;

    @Value(value = "${amqp.queue.post-delete-name}")
    private String postDeletionQueueName;

    @Value(value = "${amqp.queue.like-name}")
    private String likeQueueName;

    @Autowired
    public PostService(RabbitTemplate rabbitTemplate, AWSConfig aws, PostRepository postRepository,
                       UserService userService) {
        this.rabbitTemplate = rabbitTemplate;
        this.aws = aws;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    /**
     * Retrieves all posts.
     *
     * @return A list of all posts
     */
    public List<Post> allPosts() {
        return postRepository.findByPublishedTrue();
    }

    /**
     * Retrieves all posts by a specific user.
     *
     * @param authorId ID of the target user
     * @return A list of posts by the target user
     */
    public List<Post> allPostsByUser(String authorId) {
        User author = userService.verifyUser(authorId);

        return postRepository.findAllByAuthorAndPublishedTrue(author);
    }

    /**
     * Finds a post with the specified ID or throws an exception if none is found.
     *
     * @param postId ID of the target user
     * @return The found post
     * @throws NoSuchElementException A post with the specified ID was not found
     */
    public Post verifyPost(long postId) {
        return findPost(postId).orElseThrow(() ->
                new NoSuchElementException("A post with ID " + postId + " does not exist"));
    }

    /**
     * Determines if a post with the specified ID exists.
     *
     * @param postId ID of the target post
     * @return True if a post is found
     */
    public boolean postExists(long postId) {
        return findPost(postId).isPresent();
    }

    /**
     * Processes a post creation request by sending the post to the correct RabbitMQ queue.
     *
     * @param postDto Post to be created
     * @param media   Media that needs to be uploaded
     * @throws IOException       Media file processing error
     * @throws MimeTypeException Trying to upload a restricted file type
     */
    public void processPost(PostDto postDto, MultipartFile media) throws IOException, MimeTypeException {
        if (userService.userExists(postDto.getAuthorId())) {
            if (media != null && !media.isEmpty())
                postDto.setMediaLocation(aws.processUpload(postDto.getAuthorId(), media));

            LOGGER.info("A new post is being sent to the exchange " + exchangeName
                    + " to be routed to the queue " + postQueueName);
            rabbitTemplate.convertAndSend(postQueueName, postDto);
        } else
            throw new NoSuchElementException("A user with ID " + postDto.getAuthorId() + " does not exist");
    }

    /**
     * Process a like request for a specific post by sending the post ID to the correct RabbitMQ queue.
     *
     * @param postId  ID of the target post
     * @param addLike True to add a like or false to remove a like
     */
    public void processLike(long postId, boolean addLike) {
        LikeDto likeDto = new LikeDto(postId, addLike);

        if (postExists(likeDto.getPostId())) {
            LOGGER.info("A new like is being sent to the exchange " + exchangeName
                    + " to be routed to the queue " + likeQueueName);
            rabbitTemplate.convertAndSend(likeQueueName, likeDto);
        } else
            throw new NoSuchElementException("A post with ID " + likeDto.getPostId() + " does not exist");
    }

    /**
     * Processes a post deletion request by sending the post ID to the correct RabbitMQ queue.
     *
     * @param postId ID of the target post
     */
    public void processPostDeletion(long postId) {
        if (postExists(postId)) {
            LOGGER.info("A post deletion is being sent to exchange " + exchangeName
                    + " to be routed to the queue " + likeQueueName);
            rabbitTemplate.convertAndSend(postDeletionQueueName, postId);
        } else
            throw new NoSuchElementException("A post with ID " + postId + " does not exist");
    }

    /**
     * Helper method that searches for a specified post.
     *
     * @param postId ID of the target post
     * @return An optional Post object
     */
    private Optional<Post> findPost(long postId) {
        return postRepository.findByIdAndPublishedTrue(postId);
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

        postRepository.save(post);
        LOGGER.info("A new post with ID " + post.getId() + " has been created");
    }

    /**
     * Modifies the like count of post by consuming the like queue.
     *
     * @param likeDto Like that contains information on whether to add or remove a like
     */
    @RabbitListener(queues = "${amqp.queue.like-name}")
    private void likeModifier(LikeDto likeDto) {
        Post targetPost = verifyPost(likeDto.getPostId());

        if (likeDto.isAddLike())
            targetPost.setLikeCount(targetPost.getLikeCount() + 1);
        else
            targetPost.setLikeCount(targetPost.getLikeCount() != 0 ? targetPost.getLikeCount() - 1 : 0);

        postRepository.save(targetPost);
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
        Post targetPost = verifyPost(postId);

        if (targetPost.getHasMedia()) {
            aws.deleteMedia(targetPost.getMediaLocation());
            targetPost.setHasMedia(false);
            targetPost.setMediaLocation(null);
        }
        targetPost.setPublished(false);
        targetPost.setDeletedAt(LocalDateTime.now());

        postRepository.save(targetPost);
        LOGGER.info("Post " + postId + " has been unpublished");
    }

    /**
     * Like representation object.
     *
     * @author Kyler Deggs
     * @version 1.0.0
     */
    private static class LikeDto implements Serializable {
        @NotNull
        private final long postId;

        @NotNull
        private final boolean addLike;

        public LikeDto(long postId, boolean addLike) {
            this.postId = postId;
            this.addLike = addLike;
        }

        public long getPostId() {
            return postId;
        }

        public boolean isAddLike() {
            return addLike;
        }
    }
}