package models.user;

import enums.UserRole;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Moderator")
public class Moderator extends User {
    public Moderator(String userName, String mailAddress, String bio, String location, String profilePicture, String password, String website) {
        super(userName, mailAddress, bio, location, profilePicture, password, website, UserRole.MODERATOR);
    }

    public Moderator() {}
}
