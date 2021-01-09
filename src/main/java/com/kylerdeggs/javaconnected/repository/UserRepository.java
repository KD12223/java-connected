package com.kylerdeggs.javaconnected.repository;

import com.kylerdeggs.javaconnected.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for users.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends CrudRepository<User, String> {
}
