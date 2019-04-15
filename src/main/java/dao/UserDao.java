package dao;

import models.user.User;
import java.util.List;

public interface UserDao {
    User createUser(User user);

    User updateUser(User user);

    void removeUser(User user);

    User login(String userName, String password);

    User getUserById(Long id);

    List<User> getFollowers(Long id);

    List<User> getFollowing(Long id);

    List<User> getAllUsers();

    boolean checkToken(Long id, String token);

    List<User> searchUsers(String searchQuery);
}
