package net.omb.photogallery.security;

import net.omb.photogallery.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationListener implements ApplicationListener<AuthorizationFailureEvent> {

    private static Logger log = LoggerFactory.getLogger(AuthenticationListener.class);
    @Autowired
    private EmailService emailService;

    @Value("${mail.auth.events.enabled:false}")
    private boolean isEmailEnabled;

    @Override
    public void onApplicationEvent(AuthorizationFailureEvent event) {
        String message = "";
        Authentication authentication = event.getAuthentication();
        if(authentication.getPrincipal() instanceof JwtUser){
            message += "User: " + ((JwtUser)authentication.getPrincipal()).getUsername() + " tried authorization!";
        }
        if(authentication.getDetails() instanceof  WebAuthenticationDetails){
            message += " Ip: " +((WebAuthenticationDetails)authentication.getDetails()).getRemoteAddress();
        }

        log.info(message);
        if(isEmailEnabled) {
            emailService.sendEmailToAdmin(message);
        }
    }

}