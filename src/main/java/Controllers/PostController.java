package Controllers;

import dao.PostDao;
import dao.UserDao;
import models.post.Comment;
import models.post.Post;
import models.post.Tweet;
import models.user.User;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/post")
@Stateless
public class PostController {

    @Inject
    PostDao postDao;

    @Inject
    UserDao userDao;

    @GET
    public Response getAllPosts() {
        List<Post> posts = postDao.getAllPosts();
        return Response.ok(posts).build();
    }

    @POST
    @Path("/createTweet")
    public Response createTweetFromUserId(JsonObject json) {
        User user = userDao.getUserById(Long.parseLong(json.getString("creatorId")));
        String content = json.getString("content");

        Tweet tweet = new Tweet(content, user);
        postDao.createPost(tweet);
        user.addPost(tweet);
        userDao.updateUser(user);
        return Response.ok(tweet).build();
    }

    @POST
    @Path("/createReaction")
    @Consumes("application/json")
    public Response createResponse(JsonObject json) {
        String content = json.getString("content");
        User creator = userDao.getUserById(Long.parseLong(json.getString("creatorId")));
        Post parent = postDao.getPostById(Long.parseLong(json.getString("parentId")));

        Comment comment = new Comment(content, creator, parent);
        postDao.createPost(comment);

        parent.addComment(comment);
        creator.addPost(comment);

        postDao.updatePost(parent);
        userDao.updateUser(creator);

        return Response.ok(comment).build();
    }
}
