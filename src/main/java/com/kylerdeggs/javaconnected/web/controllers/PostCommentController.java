package com.kylerdeggs.javaconnected.web.controllers;

import com.kylerdeggs.javaconnected.service.PostCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller to handle all requests for presenting a combination of post and comment information.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
@RestController
@RequestMapping(path = "v1/api/posts_comments")
public class PostCommentController {
    private final PostCommentService postCommentService;

    @Autowired
    public PostCommentController(PostCommentService postCommentService) {
        this.postCommentService = postCommentService;
    }

    @GetMapping
    public List<PostCommentService.PostCommentDto> getAllPostsAndComments() {
        return postCommentService.allPostsAndComments();
    }

    @GetMapping(path = "/{id}")
    public List<PostCommentService.PostCommentDto> getAllPostsByUserAndComments(
            @PathVariable(value = "id") String authorId) {
        return postCommentService.allPostsByUserAndComments(authorId);
    }
}
