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

    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") Long id) {
        Post post = postDao.getPostById(id);
        if (post == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(post).build();
    }

    @POST
    @Path("/createTweet")
    public Response createTweet(JsonObject json) {
        User user = userDao.getUserById((long) json.getInt("id"));
        String content = json.getString("content");

        Tweet tweet = new Tweet(content, user);
        postDao.createPost(tweet);
        user.addPost(tweet);
        userDao.updateUser(user);
        return Response.ok(tweet).build();
    }

    @POST
    @Path("/createComment")
    @Consumes("application/json")
    public Response createResponse(JsonObject json) {
        String content = json.getString("content");
        User creator = userDao.getUserById((long) json.getInt("id"));
        Post parent = postDao.getPostById((long) json.getInt("parentId"));

        Comment comment = new Comment(content, creator, parent);
        postDao.createPost(comment);

        parent.addComment(comment);
        creator.addPost(comment);

        postDao.updatePost(parent);
        userDao.updateUser(creator);

        return Response.ok(comment).build();
    }

    @GET
    @Path("/getTimeLine/{id}")
    @Consumes("application/json")
    public Response getRelevantPosts(@HeaderParam("Authorization") String token, @PathParam("id") Long id) {
        if (userDao.checkToken(id, token)) {
            List<Post> posts = postDao.getTimeLineFromUserId(id);
            return Response.ok(posts).build();
        } else {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }
}
