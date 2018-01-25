package net.omb.photogallery.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    public JavaMailSender emailSender;

    @Value("${mail.admin}")
    private String adminMail;

    public void sendEmailToAdmin(String text) {
        log.debug("Sending email to admin");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminMail);
        message.setSubject("Photo Gallery accessed");
        message.setText(text);
        emailSender.send(message);
    }
}
