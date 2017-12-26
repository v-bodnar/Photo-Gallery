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

    @ManyToMany
    @JoinTable(name="TAGS_PHOTOS")
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


}
