package com.kylerdeggs.javaconnected.service;

import com.kylerdeggs.javaconnected.configuration.AWSConfig;
import com.kylerdeggs.javaconnected.domain.Post;
import com.kylerdeggs.javaconnected.domain.User;
import com.kylerdeggs.javaconnected.repository.PostRepository;
import com.kylerdeggs.javaconnected.security.UserSecurityContext;
import com.kylerdeggs.javaconnected.web.dtos.PostDto;
import org.apache.tika.mime.MimeTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Provides methods for retrieving, creating, updating, and deleting a post.
 *
 * @author Kyler Deggs
 * @version 1.2.1
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
     * Saves a post.
     *
     * @param post Post to save
     */
    public void savePost(Post post) {
        postRepository.save(post);
    }

    /**
     * Sends a request to the AWS service to process an upload.
     *
     * @param authorId ID of the author
     * @param media    Media that needs to be uploaded
     * @return Key of the uploaded media
     * @throws IOException       Media file processing error
     * @throws MimeTypeException Trying to upload a restricted file type
     */
    public String saveMedia(String authorId, MultipartFile media) throws IOException, MimeTypeException {
        return aws.processUpload(authorId, media);
    }

    /**
     * Sends a request to the AWS service to delete media.
     *
     * @param key Location of the media
     */
    public void deleteMedia(String key) {
        aws.deleteMedia(key);
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
        String userId = new UserSecurityContext(userService).getUser().getId();

        if (userId.equals(postDto.getAuthorId())) {
            if (media != null && !media.isEmpty())
                postDto.setMediaLocation(saveMedia(postDto.getAuthorId(), media));

            LOGGER.info("A new post is being sent to the exchange " + exchangeName
                    + " to be routed to the queue " + postQueueName);
            rabbitTemplate.convertAndSend(postQueueName, postDto);
        } else
            throw new SecurityException("The post is trying to be created with an author ID of "
                    + postDto.getAuthorId() + " but the current user has an ID of " + userId);
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
            LOGGER.info("A like message is being sent to the exchange " + exchangeName
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
        String userId = new UserSecurityContext(userService).getUser().getId();

        if (userId.equals(verifyPost(postId).getAuthor().getId())) {
            LOGGER.info("A post deletion is being sent to exchange " + exchangeName
                    + " to be routed to the queue " + likeQueueName);
            rabbitTemplate.convertAndSend(postDeletionQueueName, postId);
        } else
            throw new SecurityException("The post trying to be deleted was not created by "
                    + "the requesting user");
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
     * Like representation object.
     *
     * @author Kyler Deggs
     * @version 1.0.0
     */
    public static class LikeDto implements Serializable {
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
