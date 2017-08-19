package net.omb.photogallery.model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by volodymyr.bodnar on 7/31/2017.
 */
@Entity
public class Photo extends GenericEntity<Photo> implements Comparable<Photo> {
    private static final long serialVersionUID = 860868664458326184L;

    private String path;
    private String name;
    private String extension;
    private Long size;
    @Column(length = 4000)
    private String description;
    @OneToOne(optional=false)
    private ExifData exifData;
    @ManyToMany(mappedBy="photos")
    private List<Tag> tags;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public ExifData getExifData() {
        return exifData;
    }

    public void setExifData(ExifData exifData) {
        this.exifData = exifData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Photo photo = (Photo) o;

        if (path != null ? !path.equals(photo.path) : photo.path != null) return false;
        if (name != null ? !name.equals(photo.name) : photo.name != null) return false;
        if (extension != null ? !extension.equals(photo.extension) : photo.extension != null) return false;
        if (size != null ? !size.equals(photo.size) : photo.size != null) return false;
        if (description != null ? !description.equals(photo.description) : photo.description != null) return false;
        if (exifData != null ? !exifData.equals(photo.exifData) : photo.exifData != null) return false;
        return tags != null ? tags.equals(photo.tags) : photo.tags == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (extension != null ? extension.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (exifData != null ? exifData.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Photo" + getId() + " location:" + path;
    }

    @Override
    public int compareTo(Photo o) {
        return this.getPath().compareTo(o.getPath());
    }
}
