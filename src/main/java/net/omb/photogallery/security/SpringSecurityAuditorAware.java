package net.omb.photogallery.security;

import net.omb.photogallery.model.User;
import net.omb.photogallery.repositories.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by volodymyr.bodnar on 6/23/2017.
 */
public class SpringSecurityAuditorAware implements AuditorAware<String> {
    private static Logger log = LoggerFactory.getLogger(SpringSecurityAuditorAware.class);

    @Autowired
    private UsersRepository usersRepository;

    public String getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            //todo
            log.error("No authenticated user!");
            return null;
        }
        User user = usersRepository.findOneByEmail(((org.springframework.security.core.userdetails.User)authentication.getPrincipal()).getUsername()).orElse(null);
        if(user == null){
            //todo
            log.error("No authenticated user!");
            return null;
        }else {
            return user.getEmail();
        }

    }
}
