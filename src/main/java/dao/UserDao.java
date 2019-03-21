package dao;

import models.user.User;
import java.util.List;

public interface UserDao {
    User createUser(User user);

    User updateUser(User user);

    void removeUser(User user);

    boolean login(String userName, String password);

    User getUserById(Long id);

    List<User> getAllUsers();
}