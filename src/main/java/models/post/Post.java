package models.post;

import enums.PostType;
import models.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "POSTTYPE")
public abstract class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Date creationDate;
    private Long postCreator;
    private PostType postType;

    @ElementCollection
    private List<Long> comments = new ArrayList<Long>();

    Post(String content, User postCreator, PostType postType) {
        this.content = content;
        this.postCreator = postCreator.getId();
        this.postType = postType;
        this.creationDate = new Date();
    }

    public Post() {}

    public void addComment(Comment comment) {
        comments.add(comment.getId());
    }

    public Long getId() {
        return this.id;
    }

    public String getContent() {
        return this.content;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public Long getPostCreator() {
        return this.postCreator;
    }

    public PostType getPostType() {
        return this.postType;
    }

    public List<Long> getComments() {
        return this.comments;
    }

    @Override
    public boolean equals(Object obj) {
        Post p = (Post) obj;
        if(p == null) return false;
        return p.getId() == getId();
    }
}
