package net.omb.photogallery.services;

import net.omb.photogallery.model.Photo;
import net.omb.photogallery.model.Tag;
import net.omb.photogallery.repositories.PhotoRepository;
import net.omb.photogallery.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhotoService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private TagRepository tagRepository;

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
            return  filterOutRecursiveFiles(result, path);
        }
    }

    @Transactional
    public List<Photo> findByTagsLike(List<String> tagsNames){
        List<Tag> tags = tagRepository.findByNames(tagsNames);
        if (tags.isEmpty()) return new ArrayList<>();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Photo> cq = cb.createQuery(Photo.class);

        Root photo = cq.from(Photo.class);

        Join photoTags = photo.join("tags");
        Predicate tagsPredicate = cb.and(photoTags.in(tags));
        cq.select(photo).where(tagsPredicate);

        List<Photo> result = entityManager.createQuery(cq).getResultList();

        return result;
    }

    @Transactional
    public List<Photo> findByDateBetween(Date dateFrom, Date dateTo){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Photo> cq = cb.createQuery(Photo.class);

        Root photo = cq.from(Photo.class);
        Join exifData = photo.join("exifData");
        Predicate datePredicateFrom = cb.and(cb.ge(exifData.get("recordedDate"), dateFrom.getTime()));
        Predicate datePredicateTo = cb.and(cb.lt(exifData.get("recordedDate"), dateTo.getTime()));

        cq.select(photo).where(datePredicateFrom, datePredicateTo);

        List<Photo> result = entityManager.createQuery(cq).getResultList();

        return result;
    }

    @Transactional
    public List<Photo> findByDirectoryAndTagsLike(final String path, List<String> tagsNames,  boolean recursive){
        List<Tag> tags = tagRepository.findByNames(tagsNames);
        if (tags.isEmpty()) return new ArrayList<>();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Photo> cq = cb.createQuery(Photo.class);

        Root photo = cq.from(Photo.class);
        Predicate pathPredicate = cb.and(cb.like(photo.get("path"), "%" + path.replace("\\","\\\\") + "%"));

        Join photoTags = photo.join("tags");
        Predicate tagsPredicate = cb.and(photoTags.in(tags));
        cq.select(photo).where(pathPredicate, tagsPredicate);

        List<Photo> result = entityManager.createQuery(cq).getResultList();

        if(recursive){
            return result;
        }else {
            return  filterOutRecursiveFiles(result, path);
        }
    }

    @Transactional
    public List<Photo> findByDirectoryAndDateBetween(final String path, Date dateFrom, Date dateTo, boolean recursive){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Photo> cq = cb.createQuery(Photo.class);

        Root photo = cq.from(Photo.class);
        Predicate pathPredicate = cb.and(cb.like(photo.get("path"), "%" + path.replace("\\","\\\\") + "%"));
        Join exifData = photo.join("exifData");
        Predicate datePredicateFrom = cb.and(cb.ge(exifData.get("recordedDate"), dateFrom.getTime()));
        Predicate datePredicateTo = cb.and(cb.lt(exifData.get("recordedDate"), dateTo.getTime()));

        cq.select(photo).where(pathPredicate, datePredicateFrom, datePredicateTo);

        List<Photo> result = entityManager.createQuery(cq).getResultList();

        if(recursive){
            return result;
        }else {
            return  filterOutRecursiveFiles(result, path);
        }
    }

    @Transactional
    public List<Photo> findByTagsLikeAndDateBetween(List<String> tagsNames, Date dateFrom, Date dateTo){
        List<Tag> tags = tagRepository.findByNames(tagsNames);
        if (tags.isEmpty()) return new ArrayList<>();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Photo> cq = cb.createQuery(Photo.class);

        Root photo = cq.from(Photo.class);

        Join exifData = photo.join("exifData");
        Predicate datePredicateFrom = cb.and(cb.ge(exifData.get("recordedDate"), dateFrom.getTime()));
        Predicate datePredicateTo = cb.and(cb.lt(exifData.get("recordedDate"), dateTo.getTime()));
        Join photoTags = photo.join("tags");
        Predicate tagsPredicate = cb.and(photoTags.in(tags));
        cq.select(photo).where(tagsPredicate, datePredicateFrom, datePredicateTo);

        return entityManager.createQuery(cq).getResultList();
    }

    @Transactional
    public List<Photo> findByDirectoryAndTagsLikeAndDateBetween(final String path, List<String> tagsNames, Date dateFrom, Date dateTo, boolean recursive){
        List<Tag> tags = tagRepository.findByNames(tagsNames);
        if (tags.isEmpty()) return new ArrayList<>();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Photo> cq = cb.createQuery(Photo.class);

        Root photo = cq.from(Photo.class);
        Predicate pathPredicate = cb.and(cb.like(photo.get("path"), "%" + path.replace("\\","\\\\") + "%"));
        Join exifData = photo.join("exifData");
        Predicate datePredicateFrom = cb.and(cb.ge(exifData.get("recordedDate"), dateFrom.getTime()));
        Predicate datePredicateTo = cb.and(cb.lt(exifData.get("recordedDate"), dateTo.getTime()));
        Join photoTags = photo.join("tags");
        Predicate tagsPredicate = cb.and(photoTags.in(tags));
        cq.select(photo).where(pathPredicate, tagsPredicate, datePredicateFrom, datePredicateTo);

        List<Photo> result = entityManager.createQuery(cq).getResultList();

        if(recursive){
            return result;
        }else {
            return  filterOutRecursiveFiles(result, path);
        }
    }

    private List<Photo> filterOutRecursiveFiles(List<Photo> result, String path){
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
