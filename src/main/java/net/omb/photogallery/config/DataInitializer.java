package net.omb.photogallery.config;

import net.omb.photogallery.annotations.LogExecutionTime;
import net.omb.photogallery.model.Authority;
import net.omb.photogallery.model.Role;
import net.omb.photogallery.model.User;
import net.omb.photogallery.repositories.PhotoRepository;
import net.omb.photogallery.repositories.UsersRepository;
import net.omb.photogallery.services.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by volodymyr.bodnar on 8/18/2017.
 */
@Component
@LogExecutionTime
public class DataInitializer {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ImageService imageService;

    protected static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @PostConstruct
    public void initializeData() {
        if (!usersRepository.findById(1l).isPresent()) {
            log.info("Populating database and scanning folders");
            List<User> users = new ArrayList<>();
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            //Admin user
            String hashedPassword = passwordEncoder.encode("admin");
            User user = new User();
            user.setUsername("admin");
            user.setPassword(hashedPassword);
            user.setEnabled(true);
            user.setLastPasswordResetDate(new Date());
            Authority authority = new Authority();
            authority.setName(Role.ROLE_ADMIN);
            user.setAuthorities(Arrays.asList(authority));

            //User
            String hashedPassword1 = passwordEncoder.encode("demo");
            User user1 = new User();
            user1.setUsername("user");
            user1.setPassword(hashedPassword1);
            user1.setEnabled(true);
            user1.setLastPasswordResetDate(new Date());
            Authority authority1 = new Authority();
            authority1.setName(Role.ROLE_USER);
            user1.setAuthorities(Arrays.asList(authority1));

            users.add(user);
            users.add(user1);
            usersRepository.saveAll(users);
            try {
                imageService.scanFolderSync();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
