package com.kylerdeggs.javaconnected.web.dtos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Post representation object.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
public class PostDto implements Serializable {
    @NotNull
    private String authorId;

    @NotNull
    @Size(max = 100)
    private String title;

    @Size(max = 100)
    private String mediaLocation;

    @NotNull
    @Size(max = 3000)
    private String caption;

    public String getAuthorId() {
        return authorId;
    }

    public String getTitle() {
        return title;
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
}
