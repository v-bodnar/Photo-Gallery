package net.omb.photogallery.security;

import net.omb.photogallery.model.User;
import net.omb.photogallery.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by Vova on 6/20/2017.
 */
@Service
public class SecurityUserService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public User loadUserByUsername(String email) {
        return usersRepository.findOneByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException((String.format("User with email=%s was not found", email))));
    }
}