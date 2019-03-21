package models.post;

import enums.PostType;
import models.user.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Tweet")
public class Comment extends Post {

    private Long parentPost;

    public Comment(String content, User postCreator, Post parentPost) {
        super(content, postCreator, PostType.COMMENT);
        this.parentPost = parentPost.getId();
    }

    public Comment() { }

    public Long getParentPost() {
        return parentPost;
    }
}

