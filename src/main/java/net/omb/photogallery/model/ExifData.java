package net.omb.photogallery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.geometry.Orientation;
import net.omb.photogallery.services.ImageService;

import javax.persistence.*;

/**
 * Created by volodymyr.bodnar on 7/31/2017.
 */
@Entity
public class ExifData extends GenericEntity<ExifData>{

    private static final long serialVersionUID = -523255814275352959L;

    private String cameraModel;
    private long recordedDate;
    private int width;
    private int height;
    private String dominantColor;
    @OneToOne(optional=false, mappedBy = "exifData")
    @JsonIgnore
    private Photo photo;
    private int orientation;


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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public String getDominantColor() {
        return dominantColor;
    }

    public void setDominantColor(String dominantColor) {
        this.dominantColor = dominantColor;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

}
