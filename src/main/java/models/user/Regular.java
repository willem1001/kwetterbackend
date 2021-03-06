package models.user;

import enums.UserRole;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Regular")
public class Regular extends User {
    public Regular (String userName, String mailAddress, String bio, String location, String profilePicture, String password, String website) {
        super(userName, mailAddress, bio, location, profilePicture, password, website, UserRole.REGULAR);
    }

    public Regular() {}
}
