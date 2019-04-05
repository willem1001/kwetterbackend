package dao;

import models.post.Post;
import models.user.User;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@ApplicationScoped
public class PostImpl implements PostDao {

    private Query query;

    @PersistenceContext(unitName = "KwetterPU")
    EntityManager em;

    @Override
    public Post createPost(Post post) {
        em.persist(post);
        em.flush();
        return post;
    }

    @Override
    public Post updatePost(Post post) {
        em.merge(post);
        em.flush();
        return post;
    }

    @Override
    public void removePost(Post post) {
        em.remove(post);
    }

    @Override
    public List<Post> getAllPosts() {
        query = em.createQuery("SELECT post FROM Post post");
        return query.getResultList();
    }

    @Override
    public Post getPostById(Long id) {
        query = em.createQuery("SELECT post FROM Post post WHERE post.id = :postId");
        query.setParameter("postId", id);
        return (Post) query.getSingleResult();
    }

    @Override
    public List<Post> getPostsByCreatorId(Long id) {
        query = em.createQuery("SELECT post FROM Post post WHERE post.postCreator = :creatorId");
        query.setParameter("creatorId", id);
        return query.getResultList();
    }

    @Override
    public List<Post> getTimeLineFromUserId(Long id) {
        query = em.createQuery("SELECT post FROM Post post, User user WHERE (post.postCreator IN (SELECT uf FROM user.following uf WHERE user.id = :id)) OR post.postCreator = :id");
        query.setParameter("id",id);
        return query.getResultList();
    }
}
