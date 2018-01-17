package net.omb.photogallery.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.List;

/**
 * Created by volodymyr.bodnar on 7/31/2017.
 */
@Entity
public class Tag extends GenericEntity<Tag> {
    private static final long serialVersionUID = 1181463653530720135L;

    @Column(unique=true)
    private String name;
    @Column(length = 4000)
    private String description;


    @ManyToMany(mappedBy = "tags")
    private List<Photo> photos;

    public Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }

    public Tag(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}
