package net.omb.photogallery.config;

import net.omb.photogallery.model.Authority;
import net.omb.photogallery.model.Role;
import net.omb.photogallery.model.User;
import net.omb.photogallery.repositories.UsersRepository;
import net.omb.photogallery.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by volodymyr.bodnar on 8/18/2017.
 */
@Component
public class DataInitializer {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ImageService imageService;

    @PostConstruct
    public void initializeData() {//todo remove this
        if (!usersRepository.findById(1l).isPresent()) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode("Aq1sw2de3");
            User user = new User();
            user.setUsername("bodik@list.ru");
            user.setPassword(hashedPassword);
            user.setEnabled(true);
            user.setLastPasswordResetDate(new Date());
            Authority authority  = new Authority();
            authority.setName(Role.ADMIN);
            user.setAuthorities(Arrays.asList(authority));
            usersRepository.save(user);
        }
        try {
            imageService.scanFolderSync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
