package com.kylerdeggs.javaconnected.web;

import com.kylerdeggs.javaconnected.domain.Comment;
import com.kylerdeggs.javaconnected.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "v1/api/comments")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<Comment> getAllComments() {
        return commentService.allComments();
    }

    @GetMapping(path = "/user/{id}")
    public List<Comment> getAllCommentsByUser(@PathVariable(value = "id") String authorId) {
        return commentService.allCommentsByUser(authorId);
    }

    @GetMapping(path = "/{id}")
    public Comment getComment(@PathVariable(value = "id") long commentId) {
        return commentService.verifyComment(commentId);
    }

    @PostMapping
    public void createComment(@RequestBody @Validated CommentDto comment) {
        commentService.processComment(comment);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteComment(@PathVariable(value = "id") long commentId) {
        commentService.processCommentDeletion(commentId);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public String return404(NoSuchElementException exception) {
        return exception.getMessage();
    }
}
