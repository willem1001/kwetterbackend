package models.post;


import enums.PostType;
import models.user.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Tweet")
public class Tweet extends Post {
    public Tweet(String content, User postCreator) {
        super(content, postCreator, PostType.TWEET);
    }

    public Tweet() {}
}
