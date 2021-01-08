package com.kylerdeggs.javaconnected.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * The fields of this class represent a post comment.
 *
 * @author Kyler Deggs
 * @version 1.1.0
 */
@Entity
@Table(name = "post_comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "postId", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "authorId", nullable = false)
    private User author;

    @Column(name = "caption", nullable = false)
    private String caption;

    @Column(name = "published", nullable = false)
    private Boolean published;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;

    public Comment() {
    }

    public Comment(Post post, User author, String caption, Boolean published, LocalDateTime createdAt) {
        this.post = post;
        this.author = author;
        this.caption = caption;
        this.published = published;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
