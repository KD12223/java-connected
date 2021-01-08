package com.kylerdeggs.javaconnected.repository;

import com.kylerdeggs.javaconnected.domain.Post;
import com.kylerdeggs.javaconnected.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for posts.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long>, Queries<Post, Long> {
    /**
     * Finds all published posts.
     *
     * @return List of published posts
     */
    List<Post> findByPublishedTrue();

    /**
     * Finds all published posts by the specified user.
     *
     * @param author User to find posts from
     * @return List of published posts from the specified user
     */
    List<Post> findAllByAuthorAndPublishedTrue(User author);

    /**
     * Finds a published post with the specified ID.
     *
     * @param id ID of the target post
     * @return The published post
     */
    Optional<Post> findByIdAndPublishedTrue(Long id);
}
