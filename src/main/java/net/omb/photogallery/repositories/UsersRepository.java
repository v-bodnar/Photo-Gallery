package net.omb.photogallery.repositories;

import net.omb.photogallery.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Vova on 6/20/2017.
 */
@Repository
public interface UsersRepository extends CrudRepository<User, Long> {
    Optional<User> findOneByEmail(String email);
}
