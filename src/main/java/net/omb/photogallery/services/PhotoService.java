package net.omb.photogallery.services;

import net.omb.photogallery.model.Photo;
import net.omb.photogallery.repositories.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhotoService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PhotoRepository photoRepository;

    @Value("${root.gallery.dir}")
    private String imagesFolder;

    @Transactional
    public List<Photo> createIfNotExist(List<Photo> photosList){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Photo> cq = cb.createQuery(Photo.class);

        Root root = cq.from(Photo.class);
        Predicate pathPredicate = cb.and(root.get("path").in(photosList.stream().map(user -> user.getPath()).collect(Collectors.toList())));

        cq.select(root).where(pathPredicate);

        List<Photo> existingPhotos = entityManager.createQuery(cq).getResultList();

        if(!existingPhotos.isEmpty()){
            photosList.removeAll(existingPhotos);
        }
        if(photosList.isEmpty()){
            return new ArrayList<>();
        }
        return (List)photoRepository.saveAll(photosList);
    }

    @Transactional
    public Photo createIfNotExist(Photo photo){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Photo> cq = cb.createQuery(Photo.class);

        Root root = cq.from(Photo.class);
        Predicate pathPredicate = cb.and(cb.equal(root.get("path"), photo.getPath()));

        cq.select(root).where(pathPredicate);

        try {
            return entityManager.createQuery(cq).getSingleResult();
        }catch (NoResultException e){
            return  photoRepository.save(photo);
        }
    }

    @Transactional
    public List<Photo> findByDirectory(final String path, boolean recursive){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Photo> cq = cb.createQuery(Photo.class);

        Root root = cq.from(Photo.class);
        Predicate pathPredicate = cb.and(cb.like(root.get("path"), "%" + path.replace("\\","\\\\") + "%"));

        cq.select(root).where(pathPredicate);

        List<Photo> result = entityManager.createQuery(cq).getResultList();

        if(recursive){
            return result;
        }else {
            //We do not want images from inner directories
            java.util.function.Predicate<Photo> pathFilter = new java.util.function.Predicate<Photo>() {
                @Override
                public boolean test(Photo photo) {
                    Path relative = Paths.get(imagesFolder).relativize(Paths.get(photo.getPath()));
                    relative = Paths.get(path).relativize(relative);
                    return relative.getNameCount() == 1;
                }
            };

            return result.stream().filter(pathFilter).collect(Collectors.toList());
        }
    }
}
