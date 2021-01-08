package com.kylerdeggs.javaconnected.repository;

import com.kylerdeggs.javaconnected.domain.User;

import java.util.List;
import java.util.Optional;

/**
 * Common queries that are used to retrieve common information.
 *
 * @param <T>  Type that will be returned
 * @param <ID> ID of the specified database entity
 */
public interface Queries<T, ID> {
    List<T> findByPublishedTrue();

    List<T> findAllByAuthorAndPublishedTrue(User user);

    Optional<T> findByIdAndPublishedTrue(ID id);
}
