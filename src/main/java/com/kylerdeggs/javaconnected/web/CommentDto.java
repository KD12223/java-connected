package com.kylerdeggs.javaconnected.web;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Comment representation object.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
public class CommentDto implements Serializable {
    @NotNull
    private long postId;

    @NotNull
    private String authorId;

    @NotNull
    @Length(max = 1000)
    private String caption;

    public long getPostId() {
        return postId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getCaption() {
        return caption;
    }
}
