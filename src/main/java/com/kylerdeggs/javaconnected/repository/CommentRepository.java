package com.kylerdeggs.javaconnected.repository;

import com.kylerdeggs.javaconnected.domain.Comment;
import com.kylerdeggs.javaconnected.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for comments.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, Queries<Comment, Long> {
    /**
     * Finds all published comments.
     *
     * @return List of published comments
     */
    List<Comment> findByPublishedTrue();

    /**
     * Finds all published comments by the specified user.
     *
     * @param author User to find comments from
     * @return List of published comments from the specified user
     */
    List<Comment> findAllByAuthorAndPublishedTrue(User author);

    /**
     * Finds a published comment with the specified ID.
     *
     * @param id ID of the target comment
     * @return The published comment
     */
    Optional<Comment> findByIdAndPublishedTrue(Long id);
}
