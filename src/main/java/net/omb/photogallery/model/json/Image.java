package net.omb.photogallery.model.json;

/**
 * Created by volodymyr.bodnar on 9/17/2017.
 */
public class Image {
    private String path;
    private int width;
    private int height;

    public Image(String path, int width, int height) {
        this.path = path;
        this.width = width;
        this.height = height;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
}
