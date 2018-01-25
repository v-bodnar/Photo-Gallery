package net.omb.photogallery.controllers;

import net.omb.photogallery.config.DataInitializer;
import net.omb.photogallery.model.Authority;
import net.omb.photogallery.model.User;
import net.omb.photogallery.repositories.AuthorityRepository;
import net.omb.photogallery.repositories.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping(value = "api")
@RestController
public class UserController {
    protected static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    UsersRepository usersRepository;  //Service which will do all data retrieval/manipulation work

    @Autowired
    AuthorityRepository authorityRepository;

    //-------------------Retrieve All Users--------------------------------------------------------
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/users/", method = RequestMethod.GET)
    public ResponseEntity<List<User>> listAllUsers() {
        List<User> users = (ArrayList) usersRepository.findAll();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    //-------------------Retrieve Single User--------------------------------------------------------
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUser(@PathVariable("id") long id) {
        Optional<User> user = usersRepository.findById(id);
        if (!user.isPresent()) {
            System.out.println("User with id " + id + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user.get(), HttpStatus.OK);
    }


    //-------------------Create a User--------------------------------------------------------
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/users/", method = RequestMethod.POST)
    public ResponseEntity<Void> createUser(@RequestBody User user) {
        if (usersRepository.findByUsername(user.getUsername()).isPresent()) {
            log.error("A User with name " + user.getUsername() + " already exist");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        user.setAuthorities((ArrayList) authorityRepository.findAllById(user.getAuthorities().stream().map(authority -> authority.getId()).collect(Collectors.toList())));
        user.setLastPasswordResetDate(new Date());
        usersRepository.save(user);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    //------------------- Update a User --------------------------------------------------------
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/users/", method = RequestMethod.PUT)
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        Optional<User> currentUser = usersRepository.findById(user.getId());

        if (!currentUser.isPresent()) {
            log.error("User with id " + user.getId() + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        user.setAuthorities((ArrayList) authorityRepository.findAllById(user.getAuthorities().stream().map(authority -> authority.getId()).collect(Collectors.toList())));
        user.setLastPasswordResetDate(new Date());
        return new ResponseEntity<>(usersRepository.save(user), HttpStatus.OK);
    }

    //------------------- Delete a User --------------------------------------------------------
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<User> deleteUser(@PathVariable("id") Long id) {
        Optional<User> userFromRepo = usersRepository.findById(id);

        if (!userFromRepo.isPresent()) {
            System.out.println("Unable to delete. User with id " + id + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Authority> authorities = (ArrayList) authorityRepository.findAllById(userFromRepo.get().getAuthorities().stream().map(authority -> authority.getId()).collect(Collectors.toList()));
        for (Authority authority : authorities) {
            authority.getUsers().remove(userFromRepo.get());
        }
        authorityRepository.saveAll(authorities);
        usersRepository.delete(userFromRepo.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/authorities/", method = RequestMethod.GET)
    public ResponseEntity<List<Authority>> listAllAuthorities() {
        List<Authority> authorities = (ArrayList) authorityRepository.findAll();
        if (authorities.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(authorities, HttpStatus.OK);
    }

}