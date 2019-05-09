package Controllers;

import com.google.gson.Gson;
import dao.PostDao;
import dao.UserDao;
import models.post.Comment;
import models.post.Post;
import models.post.Tweet;
import models.user.User;
import org.json.JSONObject;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/api/post")
@Stateless
public class PostController {

    @Inject
    PostDao postDao;

    @Inject
    UserDao userDao;

    @GET
    public Response getAllPosts(@HeaderParam("Authorization") String token, @HeaderParam("AuthorizationId") Long authorizationId) {

        if (!userDao.checkToken(authorizationId, token)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        List<Post> posts = postDao.getAllPosts();
        return Response.ok(posts).build();
    }

    @GET
    @Path("/{id}")
    public Response getUser(@HeaderParam("Authorization") String token, @HeaderParam("AuthorizationId") Long authorizationId, @PathParam("id") Long id) {

        if (!userDao.checkToken(authorizationId, token)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        Post post = postDao.getPostById(id);
        if (post == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(post).build();
    }

    @POST
    @Consumes("application/json")
    @Path("/createTweet")
    public Response createTweet(@HeaderParam("Authorization") String token, @HeaderParam("AuthorizationId") Long authorizationId, JsonObject json) {

        if (!userDao.checkToken(authorizationId, token)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

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
    public Response createResponse(@HeaderParam("Authorization") String token, @HeaderParam("AuthorizationId") Long authorizationId, JsonObject json) {

        if (!userDao.checkToken(authorizationId, token)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

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
    public Response getRelevantPosts(@HeaderParam("Authorization") String token, @HeaderParam("AuthorizationId") Long authorizationId, @PathParam("id") Long id) {
        if (!userDao.checkToken(authorizationId, token)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
        List<Post> posts = postDao.getTimeLineFromUserId(id);
        List<String> tweetsJson = new ArrayList<>();

        for (Post tweet : posts) {
            List<Post> comments = postDao.getCommentsFromParentId(tweet.getId());
            List<JSONObject> commentsJson = new ArrayList<>();

            for (Post comment : comments) {
                User commentCreator = userDao.getUserById(comment.getPostCreator());
                JSONObject commentJson = new JSONObject()
                        .put("commentId", comment.getId())
                        .put("commentCreator", new JSONObject()
                                .put("creatorName", commentCreator.getUserName())
                                .put("creatorId", commentCreator.getId()))
                        .put("commentContent", comment.getContent())
                        .put("commentDate", comment.getCreationDate());
                commentsJson.add(commentJson);
            }

            User tweetCreator = userDao.getUserById(tweet.getPostCreator());
            JSONObject tweetJson = new JSONObject()
                    .put("tweetId", tweet.getId())
                    .put("tweetContent", tweet.getContent())
                    .put("tweetDate", tweet.getCreationDate())
                    .put("tweetCreator", new JSONObject()
                            .put("creatorName", tweetCreator.getUserName())
                            .put("creatorId", tweetCreator.getId()))
                    .put("comments", commentsJson);
            tweetsJson.add(tweetJson.toString());
        }

        return Response.ok(tweetsJson).build();
    }
}
