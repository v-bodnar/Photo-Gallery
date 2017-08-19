package net.omb.photogallery.repositories;

import net.omb.photogallery.model.Photo;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by volodymyr.bodnar on 7/31/2017.
 */
public interface PhotoRepository extends CrudRepository<Photo, Long> {
}