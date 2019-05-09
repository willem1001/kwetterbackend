package dao;

import enums.PostType;
import models.post.Post;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
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

    @Transactional
    @Override
    public void removePost(Post post) {
        Post p = em.merge(post);
        em.remove(p);
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
        query = em.createQuery("SELECT DISTINCT tweet FROM Tweet tweet, User user WHERE(tweet.postCreator IN (SELECT uf FROM user.following uf WHERE user.id = :id) OR tweet.postCreator = :id)");
        query.setParameter("id", id);
        return query.getResultList();
    }

    @Override
    public List<Post> getCommentsFromParentId(Long id) {
        query = em.createQuery("SELECT comment FROM Comment comment WHERE comment.parentPost = :id");
        query.setParameter("id", id);
        return query.getResultList();
    }
}
