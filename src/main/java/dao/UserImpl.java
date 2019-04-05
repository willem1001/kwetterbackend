package dao;

import models.post.Post;
import models.user.User;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@ApplicationScoped
public class UserImpl implements UserDao {

    private Query query;

    @PersistenceContext(unitName = "KwetterPU")
    EntityManager em;

    @Override
    public User createUser(User user) {
        em.persist(user);
        em.flush();
        return user;
    }

    @Override
    public User updateUser(User user) {
        em.merge(user);
        em.flush();
        return user;
    }

    @Override
    public void removeUser(User user) {
        em.remove(user);
    }

    @Override
    public User login(String userName, String password) {
        query = em.createQuery("SELECT user FROM User user WHERE user.userName = :userName");
        query.setParameter("userName", userName);
        User u = (User) query.getSingleResult();
        if(u.getUserName().equals(userName) && u.getPassword().equals(password)) {
            return u;
        }
        return null;
    }

    @Override
    public User getUserById(Long id) {
        query = em.createQuery("SELECT user FROM User user WHERE user.id = :id");
        query.setParameter("id", id);

        return (User) query.getSingleResult();
    }

    @Override
    public List<User> getFollowers(Long id) {
        query = em.createQuery("SELECT user FROM User user WHERE user.following IN (SELECT uf FROM user.following uf)AND user.id = :id");
        query.setParameter("id", id);
        return query.getResultList();
    }

    @Override
    public List<User> getAllUsers() {
        query = em.createQuery("SELECT user FROM User user");
        return query.getResultList();
    }
}
