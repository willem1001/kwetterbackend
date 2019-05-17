package mail;

import models.user.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Pattern;

public class Mail {

    private static Pattern REGEX = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");
    private static Session session;
    private static Properties props;

    public static Boolean sendMail(User user) {
        try {

            if(!REGEX.matcher(user.getMailAddress()).matches()) {
                return false;
            }

            if (props == null) {
                props = getProperties();
            }
            if (session == null) {
                session = createSession();
            }

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(props.getProperty("mail.address")));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(user.getMailAddress(), false));
            msg.setSubject("Kwetter registration");

            StringBuilder builder = new StringBuilder();
            builder.append("Hello " + user.getUserName() + ", \n \n")
            .append("Please follow the following link to activate your account. \n")
            .append("http://localhost:4200/activation/" + user.getActivationToken());


            msg.setText(builder.toString());
            msg.setSentDate(new Date());
            Transport.send(msg);
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private static Session createSession() {
        return Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(props.getProperty("mail.address"), props.getProperty("mail.password"));
                    }
                });
    }

    private static Properties getProperties() {
        final Properties props = new Properties();
        try {
            props.load(new FileInputStream("E:\\GitE\\kwetterbackend\\src\\main\\java\\mail\\mailproperties.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }
}
