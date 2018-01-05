package net.omb.photogallery.config;

import net.omb.photogallery.annotations.WithUser;
import net.omb.photogallery.model.Role;
import net.omb.photogallery.model.User;
import net.omb.photogallery.repositories.UsersRepository;
import net.omb.photogallery.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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
            User user = new User("bodik@list.ru", hashedPassword, Role.ADMIN, true);
            usersRepository.save(user);
        }
        try {
            imageService.scanFolderSync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
