package com.kylerdeggs.javaconnected.web.controllers;

import com.kylerdeggs.javaconnected.domain.Comment;
import com.kylerdeggs.javaconnected.service.CommentService;
import com.kylerdeggs.javaconnected.web.dtos.CommentDto;
import com.kylerdeggs.javaconnected.web.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller to handle all requests pertaining to a comment.
 *
 * @author Kyler Deggs
 * @version 1.2.1
 */
@RestController
@RequestMapping("v1/api/comments")
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

    @GetMapping("/user/{id}")
    public List<Comment> getAllCommentsByUser(@PathVariable("id") String authorId) {
        return commentService.allCommentsByUser(authorId);
    }

    @GetMapping("/{id}")
    public Comment getComment(@PathVariable("id") long commentId) {
        return commentService.verifyComment(commentId);
    }

    @PostMapping
    public ResponseEntity<HttpResponse> createComment(@RequestBody @Validated CommentDto comment) {
        commentService.processComment(comment);

        return ResponseEntity.accepted().body(new HttpResponse(HttpStatus.ACCEPTED.getReasonPhrase(),
                "Comment creation request has been accepted"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse> deleteComment(@PathVariable("id") long commentId) {
        commentService.processCommentDeletion(commentId);

        return ResponseEntity.accepted().body(new HttpResponse(HttpStatus.ACCEPTED.getReasonPhrase(),
                "Comment deletion request for comment " + commentId + " has been accepted"));
    }
}
