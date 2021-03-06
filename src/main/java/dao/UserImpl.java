package dao;

import models.user.User;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
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

    @Transactional
    @Override
    public User updateUser(User user) {
        em.merge(user);
        em.flush();
        return user;
    }

    @Transactional
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
    public User getUserByUserName(String userName) {
        query = em.createQuery("SELECT user FROM User user WHERE user.userName = :userName");
        query.setParameter("userName", userName);

        return (User) query.getSingleResult();
    }

    @Override
    public User getUserByActivationToken(String activationToken) {
        query = em.createQuery("SELECT user FROM User user WHERE user.activationToken = :activationToken");
        query.setParameter("activationToken", activationToken);
        return (User) query.getSingleResult();
    }

    @Override
    public List<User> getFollowers(Long id) {
        query = em.createQuery("SELECT user.followers FROM User user WHERE user.id = :id");
        query.setParameter("id", id);

        List followersIds =  query.getResultList();

        query = em.createQuery("SELECT user FROM User user WHERE user.id IN :followingIds");
        query.setParameter("followingIds", followersIds);
        return query.getResultList();
    }

    @Override
    public List<User> getFollowing(Long id) {
        query = em.createQuery("SELECT user.following FROM User user WHERE user.id = :id");
        query.setParameter("id", id);

        List followingIds =  query.getResultList();

        query = em.createQuery("SELECT user FROM User user WHERE user.id IN :followingIds");
        query.setParameter("followingIds", followingIds);
        return query.getResultList();
    }

    @Override
    public List<User> getAllUsers() {
        query = em.createQuery("SELECT user FROM User user");
        return query.getResultList();
    }

    @Override
    public boolean checkToken(Long id, String token) {
        query = em.createQuery("SELECT user.token FROM User user WHERE user.id = :id");
        query.setParameter("id", id);
        String userToken = query.getSingleResult().toString();
        return userToken.equals(token);
    }

    @Override
    public List<User> searchUsers(String searchQuery) {
        query = em.createQuery("SELECT user FROM User user WHERE user.userName LIKE :searchQuery");
        query.setParameter("searchQuery", "%" + searchQuery + "%");
        return query.getResultList();
    }
}
