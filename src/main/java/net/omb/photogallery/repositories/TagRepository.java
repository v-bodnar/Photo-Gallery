package net.omb.photogallery.repositories;

import net.omb.photogallery.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Long> {
    List<Tag> findByName(String name);

    @Query( "SELECT t FROM Tag t where name in :names" )
    List<Tag> findByNames(@Param("names") List<String> names);
}
