package Controllers;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import dao.PostDao;
import dao.UserDao;
import enums.UserRole;
import mail.Mail;
import models.user.Regular;
import models.user.User;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/user")
@Stateless
public class UserController {

    @Inject
    private UserDao userDao;

    @Inject
    private PostDao postDao;

    @GET
    public Response getAllUsers(@HeaderParam("Authorization") String token, @HeaderParam("AuthorizationId") Long authorizationId) {

        if (!userDao.checkToken(authorizationId, token)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        List<User> users = userDao.getAllUsers();
        return Response.ok(users).build();
    }

    @GET
    @Path("/{id}")
    public Response getUser(@HeaderParam("Authorization") String token, @HeaderParam("AuthorizationId") Long authorizationId, @PathParam("id") Long id) {

        if (!userDao.checkToken(authorizationId, token)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        User user = userDao.getUserById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(user).build();
    }

    @POST
    @Path("/login")
    @Consumes("application/json")
    public Response login(JsonObject json) {
        String userName = json.getString("userName");
        String password = json.getString("password");
        User u = userDao.login(userName, password);

        if (u.getActivated()) {
            if (!password.equals(u.getPassword())) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            String token = Hashing.sha512().hashString(userName + System.currentTimeMillis(), Charsets.UTF_8).toString();
            u.setToken(token);
            userDao.updateUser(u);
            return Response.ok(u).build();
        }
        return Response.ok("not activated").build();
    }

    @POST
    @Path("/logout")
    @Consumes("application/json")
    public Response logout(@HeaderParam("Authorization") String token, @HeaderParam("AuthorizationId") Long authorizationId, JsonObject json) {

        if (!userDao.checkToken(authorizationId, token)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        User user = userDao.getUserById((long) json.getInt("id"));
        user.setToken("");
        userDao.updateUser(user);
        return Response.ok().build();
    }

    @POST
    @Path("/follow")
    @Consumes("application/json")
    public Response followUser(@HeaderParam("Authorization") String token, @HeaderParam("AuthorizationId") Long authorizationId, JsonObject json) {

        if (!userDao.checkToken(authorizationId, token)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        User follower = userDao.getUserById((long) json.getInt("followerId"));
        User following = userDao.getUserById((long) json.getInt("followingId"));

        if (follower.getFollowing().contains(following.getId())) {
            follower.unfollowUser(following);
            return Response.ok().build();
        }

        follower.followUser(following);
        userDao.updateUser(follower);
        userDao.updateUser(following);

        return Response.ok(following).build();
    }

    @POST
    @Path("/create")
    @Consumes("application/json")
    public Response createUser(JsonObject json) {
        Regular regular = new Gson().fromJson(json.toString(), Regular.class);
        regular.setUserRole(UserRole.REGULAR);

        String activationToken = Hashing.sha512().hashString((Math.random() + "" + System.currentTimeMillis()), Charsets.UTF_8).toString();
        regular.setActivationToken(activationToken);

        userDao.createUser(regular);
        Mail.sendMail(regular);
        return Response.ok(regular).build();
    }

    @POST
    @Path("/activate")
    @Consumes("application/json")
    public Response activateUser(JsonObject json) {
        User user = userDao.getUserByActivationToken(json.getString("activationToken"));
        user.activateUser();
        userDao.updateUser(user);
        return Response.ok(Response.Status.ACCEPTED).build();
    }

    @GET
    @Path("/searchUsers/{query}")
    @Consumes("application/json")
    public Response searchUsers(@HeaderParam("Authorization") String token, @HeaderParam("AuthorizationId") Long authorizationId, @PathParam("query") String query) {
        if (!userDao.checkToken(authorizationId, token)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        List<User> foundUsers = userDao.searchUsers(query);
        return Response.ok(foundUsers).build();
    }

    @GET
    @Path("/getAllFollowers/{id}")
    @Consumes("application/json")
    public Response getAllFollowers(@HeaderParam("Authorization") String token, @HeaderParam("AuthorizationId") Long authorizationId, @PathParam("id") Long id) {
        if (!userDao.checkToken(authorizationId, token)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
        List<User> following = userDao.getFollowers(id);
        return Response.ok(following).build();
    }

    @GET
    @Path("/getAllFollowing/{id}")
    @Consumes("application/json")
    public Response getAllFollowing(@HeaderParam("Authorization") String token, @HeaderParam("AuthorizationId") Long authorizationId, @PathParam("id") Long id) {
        if (!userDao.checkToken(authorizationId, token)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
        List<User> following = userDao.getFollowing(id);
        return Response.ok(following).build();
    }
}

