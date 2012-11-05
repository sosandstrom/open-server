package com.wadpam.open.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * This class is responsible for sending email.
 * @author mattiaslevin
 */
public class EmailSender {

    static final Logger LOG = LoggerFactory.getLogger(AppService.class);

    // Email from information
    private String fromEmailAddress;
    private String fromEmailName;


    // Send email to admin
    public boolean sendEmailToAdmin(String subject, String body) {
        return sendEmail(getFromEmailAddress(), getFromEmailName(), subject, body);
    }

    // Send an email
    public boolean sendEmail(String toEmail, String toName, String subject, String body) {
        LOG.debug("Send email to:{} with subject:{}", toEmail, toName);
        LOG.debug("Email from address:{} from name:{}", fromEmailAddress, fromEmailName);

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromEmailAddress, fromEmailName));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail, toName));
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);

            return true;
        } catch (Exception e) {
            // Catch all exceptions and just log an error, do not interrupt flow
            LOG.error("Not possible to send email with reason:{}", e.getMessage());
            return false;
        }
    }


    // Setter and getter
    public String getFromEmailAddress() {
        return fromEmailAddress;
    }

    public void setFromEmailAddress(String fromEmailAddress) {
        this.fromEmailAddress = fromEmailAddress;
    }

    public String getFromEmailName() {
        return fromEmailName;
    }

    public void setFromEmailName(String fromEmailName) {
        this.fromEmailName = fromEmailName;
    }
}
