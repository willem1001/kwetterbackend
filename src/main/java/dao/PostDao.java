package dao;

import models.post.Post;
import java.util.List;

public interface PostDao {
    Post createPost(Post post);

    Post updatePost(Post post);

    void removePost(Post post);

    List<Post> getAllPosts();

    Post getPostById(Long id);

    List<Post> getPostsByCreatorId(Long id);
}
