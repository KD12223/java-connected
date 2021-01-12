package com.kylerdeggs.javaconnected.security;

import com.kylerdeggs.javaconnected.domain.User;
import com.kylerdeggs.javaconnected.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Stores a User object that represents the user sending the request and stores their JSON Web Token.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
public class UserSecurityContext {
    private final User user;

    private final Jwt jwt;


    public UserSecurityContext(UserService userService) {
        jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userService.verifyUser(jwt.getClaimAsString("uid"));
    }

    public User getUser() {
        return user;
    }

    public Jwt getJwt() {
        return jwt;
    }
}
