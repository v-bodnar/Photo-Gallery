package net.omb.photogallery.repositories;

import net.omb.photogallery.model.ExifData;
import org.springframework.data.repository.CrudRepository;

public interface ExifDataRepository extends CrudRepository<ExifData, Long> {
}
