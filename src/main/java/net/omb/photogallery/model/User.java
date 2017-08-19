package net.omb.photogallery.model;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by volodymyr.bodnar on 6/19/2017.
 */
@Entity(name = "users")
public class User extends GenericEntity<User> implements org.springframework.security.core.userdetails.UserDetails, CredentialsContainer {
    private static final long serialVersionUID = -8573472150562508317L;

    @Column(unique = true)
    private String email;
    @Column(nullable = false)
    @NotNull
    private String password;
    private HashSet<GrantedAuthority> authorities;
    private boolean expired;
    private boolean credentialsExpired;
    private boolean locked;
    private boolean enabled;
    private Role role;

    private User(){}

    @PersistenceConstructor
    public User(String email, String password, Role role, boolean enabled) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.authorities = new HashSet<>(AuthorityUtils.createAuthorityList(role.name()));
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void eraseCredentials() {
        password = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
