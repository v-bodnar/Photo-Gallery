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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by volodymyr.bodnar on 8/18/2017.
 */
@Component
@LogExecutionTime
public class DataInitializer {
    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private ImageService imageService;

    protected static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @PostConstruct
    public void initializeData() {
        if (!photoRepository.findById(1l).isPresent()) {
            log.info("Populating database and scanning folders");
            imageService.scanFolderSync();
        }
    }

}
