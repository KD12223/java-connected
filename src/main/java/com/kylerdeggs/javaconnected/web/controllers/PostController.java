package com.kylerdeggs.javaconnected.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kylerdeggs.javaconnected.domain.Post;
import com.kylerdeggs.javaconnected.service.PostService;
import com.kylerdeggs.javaconnected.web.HttpResponse;
import com.kylerdeggs.javaconnected.web.dtos.PostDto;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controller to handle all requests pertaining to a post.
 *
 * @author Kyler Deggs
 * @version 1.2.1
 */
@RestController
@RequestMapping("v1/api/posts")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.allPosts();
    }

    @GetMapping("/user/{id}")
    public List<Post> getAllPostsByUser(@PathVariable(value = "id") String authorId) {
        return postService.allPostsByUser(authorId);
    }

    @GetMapping("/{id}")
    public Post getPost(@PathVariable("id") long postId) {
        return postService.verifyPost(postId);
    }

    @PostMapping
    public ResponseEntity<HttpResponse> createPost(@RequestParam(value = "media", required = false) MultipartFile media,
                                                   @RequestParam("post") String postInformation) throws IOException, MimeTypeException {
        PostDto postDto = new ObjectMapper().readValue(postInformation, PostDto.class);

        postService.processPost(postDto, media);

        return ResponseEntity.accepted().body(new HttpResponse(HttpStatus.ACCEPTED.getReasonPhrase(),
                "Post creation request has been accepted"));
    }

    @PatchMapping("/likes")
    public ResponseEntity<HttpResponse> modifyLikes(@RequestParam("postId") long postId,
                                                    @RequestParam("addLike") boolean addLike) {
        postService.processLike(postId, addLike);

        return ResponseEntity.accepted().body(new HttpResponse(HttpStatus.ACCEPTED.getReasonPhrase(),
                "Like " + (addLike ? "creation" : "deletion") + " request for post "
                        + postId + " has been accepted"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse> deletePost(@PathVariable("id") long postId) {
        postService.processPostDeletion(postId);

        return ResponseEntity.accepted().body(new HttpResponse(HttpStatus.ACCEPTED.getReasonPhrase(),
                "Post deletion request for post " + postId + " has been accepted"));
    }
}
