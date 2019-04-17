package jsf;

import dao.PostDao;
import dao.UserDao;
import enums.PostType;
import enums.UserRole;
import javafx.geometry.Pos;
import models.post.Comment;
import models.post.Post;
import models.post.Tweet;
import models.user.User;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import java.util.List;

@ManagedBean
@SessionScoped
public class AdminBean {
    @Inject
    UserDao userDao;

    @Inject
    PostDao postDao;

    private List<User> allUsers;
    private String postId;

    private String userName;
    private UserRole userRole;

    public List<User> getAllUsers() {
        return allUsers;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public void init() {
        allUsers = userDao.getAllUsers();
    }

    public void deleteTweetById() {
        Post post = postDao.getPostById(Long.parseLong(postId));
        postDao.removePost(post);
    }

    public void changeUserRole(){
        User user = userDao.getUserByUserName(userName);
        user.setUserRole(userRole);
        userDao.updateUser(user);
    }
}
