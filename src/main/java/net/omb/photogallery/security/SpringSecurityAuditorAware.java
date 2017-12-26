package net.omb.photogallery.security;

import net.omb.photogallery.model.User;
import net.omb.photogallery.repositories.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Created by volodymyr.bodnar on 6/23/2017.
 */
public class SpringSecurityAuditorAware implements AuditorAware<String> {
    private static Logger log = LoggerFactory.getLogger(SpringSecurityAuditorAware.class);

    @Autowired
    private UsersRepository usersRepository;

    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            //todo
            log.error("No authenticated user!");
            return null;
        }
        if(authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            if ("bodik@list.ru".equals(user.getEmail()) && user.getId() == 0) {
                return Optional.of(user.getUsername());
            }
        }
        User user = usersRepository.findOneByEmail(((org.springframework.security.core.userdetails.User)authentication.getPrincipal()).getUsername()).get();
        return Optional.of(user.getUsername());

    }
}
