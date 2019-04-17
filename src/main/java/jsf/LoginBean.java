package jsf;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import dao.UserDao;
import enums.UserRole;
import models.user.User;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import java.nio.charset.Charset;

@ManagedBean
@SessionScoped
public class LoginBean {

    @Inject
    UserDao userDao;

    private String userName;
    private String password;
    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String login() {
        String urlArgs = "?faces-redirect=true";

        String hash = Hashing.sha512().hashString(password, Charsets.UTF_8).toString();
        currentUser = userDao.login(userName, hash);

       if(currentUser == null || currentUser.getUserRole() != UserRole.MODERATOR) {
           return "index" + urlArgs;
       }
       return "adminpage" + urlArgs;
    }
}
