package net.omb.photogallery.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Created by volodymyr.bodnar on 7/31/2017.
 */
@Entity
public class ExifData extends GenericEntity<ExifData>{

    private static final long serialVersionUID = -523255814275352959L;

    private String cameraModel;
    private long recordedDate;
    private long width;
    private long height;
    @OneToOne(mappedBy = "exifData")
    private Photo photo;

    public String getCameraModel() {
        return cameraModel;
    }

    public void setCameraModel(String cameraModel) {
        this.cameraModel = cameraModel;
    }

    public long getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(long recordedDate) {
        this.recordedDate = recordedDate;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }
}
