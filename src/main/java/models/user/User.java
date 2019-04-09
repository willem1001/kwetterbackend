package models.user;

import enums.UserRole;
import models.post.Post;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ROLE")
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String userName;
    private String bio;
    private String location;
    private String profilePicture;
    private String password;
    private String website;
    private String token;
    private UserRole userRole;

    @ElementCollection
    private List<Long> following = new ArrayList<Long>();

    @ElementCollection
    private List<Long> followers = new ArrayList<Long>();

    @ElementCollection
    private List<Long> createdPosts = new ArrayList<Long>();

    public User(String userName, String bio, String location, String profilePicture, String password, String website, UserRole userRole) {
        this.userName = userName;
        this.bio = bio;
        this.location = location;
        this.profilePicture = profilePicture;
        this.password = password;
        this.website = website;
        this.userRole = userRole;
    }

    public User() {
    }

    public void followUser(User user) {
        this.following.add(user.id);
        user.followers.add(this.id);
    }

    public void addPost(Post post) {
        this.createdPosts.add(post.getId());
    }

    public Long getId() {
        return this.id;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getBio() {
        return this.bio;
    }

    public String getLocation() {
        return this.location;
    }

    public String getProfilePicture() {
        return this.profilePicture;
    }

    public String getPassword() {
        return this.password;
    }

    public String getWebsite() {
        return this.website;
    }

    public String getToken() {
        return this.token;
    }

    public UserRole getUserRole() {
        return this.userRole;
    }

    public List<Long> getFollowing() {
        return this.following;
    }

    public List<Long> getFollowers() {
        return this.followers;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }


    @Override
    public boolean equals(Object obj) {
        User u = (User) obj;
        if (u == null) return false;
        return u.getId() == getId();
    }
}
