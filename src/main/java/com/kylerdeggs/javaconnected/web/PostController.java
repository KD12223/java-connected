package com.kylerdeggs.javaconnected.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kylerdeggs.javaconnected.domain.Post;
import com.kylerdeggs.javaconnected.service.PostService;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "v1/api/posts")
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

    @GetMapping(path = "/user/{id}")
    public List<Post> getAllPostsByUser(@PathVariable(value = "id") String authorId) {
        return postService.allPostsByUser(authorId);
    }

    @GetMapping(path = "/{id}")
    public Post getPost(@PathVariable(value = "id") long postId) {
        return postService.verifyPost(postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void createPost(@RequestParam(value = "media", required = false) MultipartFile media,
                           @RequestParam(value = "post") String postInformation) throws IOException, MimeTypeException {
        PostDto postDto = new ObjectMapper().readValue(postInformation, PostDto.class);

        postService.processPost(postDto, media);
    }

    @PatchMapping(path = "/likes")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void modifyLikes(@RequestParam(value = "postId") long postId,
                            @RequestParam(value = "addLike") boolean addLike) {
        postService.processLike(postId, addLike);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deletePost(@PathVariable(value = "id") long postId) {
        postService.processPostDeletion(postId);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public String return404(NoSuchElementException exception) {
        return exception.getMessage();
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(IllegalArgumentException.class)
    public String return415(IllegalArgumentException exception) {
        return exception.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IOException.class)
    public String return500(IOException exception) {
        return exception.getMessage();
    }
}