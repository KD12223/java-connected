package com.kylerdeggs.javaconnected.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * The fields of this class represent a post.
 *
 * @author Kyler Deggs
 * @version 1.1.0
 */
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "authorId", nullable = false)
    private User author;

    @Column(name = "title", length = 45, nullable = false)
    private String title;

    @Column(name = "media", nullable = false)
    private Boolean hasMedia;

    @Column(name = "mediaLocation", length = 100)
    private String mediaLocation;

    @Column(name = "caption", length = 3000)
    private String caption;

    @Column(name = "likeCount")
    private Integer likeCount;

    @Column(name = "published", nullable = false)
    private Boolean published;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;

    public Post() {
    }

    public Post(User author, String title, Boolean hasMedia, String mediaLocation, String caption,
                Boolean published, LocalDateTime createdAt) {
        this.author = author;
        this.title = title;
        this.hasMedia = hasMedia;
        this.mediaLocation = mediaLocation;
        this.caption = caption;
        likeCount = 0;
        this.published = published;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getHasMedia() {
        return hasMedia;
    }

    public void setHasMedia(Boolean hasMedia) {
        this.hasMedia = hasMedia;
    }

    public String getMediaLocation() {
        return mediaLocation;
    }

    public void setMediaLocation(String mediaLocation) {
        this.mediaLocation = mediaLocation;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
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
