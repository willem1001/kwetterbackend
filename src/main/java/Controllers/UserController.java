package Controllers;

import com.sun.deploy.net.HttpResponse;
import dao.PostDao;
import dao.UserDao;
import enums.UserRole;
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
    public Response getAllUsers() {
        List<User> users = userDao.getAllUsers();
        return Response.ok(users).build();
    }

    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") Long id) {
        User user = userDao.getUserById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(user).build();
        }
    }

    @POST
    @Path("/follow")
    @Consumes("application/json")
    public Response followUser(JsonObject json) {
        User follower = userDao.getUserById(Long.parseLong(json.getString("followerId")));
        User following = userDao.getUserById(Long.parseLong(json.getString("followingId")));

        follower.followUser(following);

        userDao.updateUser(follower);
        userDao.updateUser(following);

        return Response.ok(follower).build();
    }

    @POST
    @Path("/create")
    @Consumes("application/json")
    public Response createUser(JsonObject json) {
        Regular regular = new Regular();
        regular.setUserName(json.getString("userName"));
        regular.setBio(json.getString("bio"));
        regular.setLocation(json.getString("location"));
        regular.setPassword(json.getString("password"));
        regular.setProfilePicture(json.getString("profilePicture"));
        regular.setWebsite(json.getString("website"));
        regular.setUserRole(UserRole.REGULAR);
        userDao.createUser(regular);
        return Response.ok(regular).build();
    }
}
