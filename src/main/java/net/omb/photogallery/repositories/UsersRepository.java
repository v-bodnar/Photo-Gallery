package net.omb.photogallery.repositories;

import net.omb.photogallery.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by Vova on 6/20/2017.
 */
public interface UsersRepository extends CrudRepository<User, Long> {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Optional<User> findByUsername(String username);
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Optional<User> findOneByUsernameAndPassword(String username, String password);
}
