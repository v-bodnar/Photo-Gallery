package net.omb.photogallery.model.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.omb.photogallery.model.Photo;
import net.omb.photogallery.services.ImageService;
import net.omb.photogallery.utils.PropertiesUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import static net.omb.photogallery.services.ImageService.Size.*;

/**
 * Created by volodymyr.bodnar on 9/17/2017.
 */

public class Preview {
    @JsonIgnore
    private static final Path BASE_DIR = Paths.get((String) PropertiesUtil.getProperty("root.gallery.dir"));
    @JsonIgnore
    private static final String HOST = (String) PropertiesUtil.getProperty("root.context.path");
    @JsonIgnore
    private static final String IMG_DOWNLOAD_SERVICE = (String) PropertiesUtil.getProperty("root.context.path");
    @JsonIgnore
    private static final boolean xxsEnabled = (Boolean) PropertiesUtil.getProperty("size.xxs.enabled");
    @JsonIgnore
    private static final boolean xsEnabled = (Boolean) PropertiesUtil.getProperty("size.xs.enabled");
    @JsonIgnore
    private static final boolean sEnabled = (Boolean) PropertiesUtil.getProperty("size.s.enabled");
    @JsonIgnore
    private static final boolean mEnabled = (Boolean) PropertiesUtil.getProperty("size.m.enabled");
    @JsonIgnore
    private static final boolean lEnabled = (Boolean) PropertiesUtil.getProperty("size.l.enabled");
    @JsonIgnore
    private static final boolean xlEnabled = (Boolean) PropertiesUtil.getProperty("size.xl.enabled");

    @JsonProperty("prewiew_xxs")
    private Image previewXXS;
    @JsonProperty("prewiew_xs")
    private Image previewXS;
    @JsonProperty("prewiew_s")
    private Image previewS;
    @JsonProperty("prewiew_m")
    private Image previewM;
    @JsonProperty("prewiew_l")
    private Image previewL;
    @JsonProperty("prewiew_xl")
    private Image previewXL;
    private Image raw;
    private String dominantColor;

    public Preview() {
    }

    public Preview(Photo photo) {
        int width = photo.getExifData().getWidth();
        int height = photo.getExifData().getHeight();
        this.setRaw(new Image(formDownloadUrl(photo.getPath(), RAW), width, height));
        this.setPreviewXXS(new Image(formDownloadUrl(photo.getPath(), XXS), calculateWidth(width, height, XXS.getHeight()), XXS.getHeight()));
        this.setPreviewXS(new Image(formDownloadUrl(photo.getPath(), XS), calculateWidth(width, height, XS.getHeight()), XS.getHeight()));
        this.setPreviewS(new Image(formDownloadUrl(photo.getPath(), S), calculateWidth(width, height, S.getHeight()), S.getHeight()));
        this.setPreviewM(new Image(formDownloadUrl(photo.getPath(), M), calculateWidth(width, height, M.getHeight()), M.getHeight()));
        this.setPreviewL(new Image(formDownloadUrl(photo.getPath(), L), calculateWidth(width, height, L.getHeight()), L.getHeight()));
        this.setPreviewXL(new Image(formDownloadUrl(photo.getPath(), XL), calculateWidth(width, height, XL.getHeight()), XL.getHeight()));
        this.setDominantColor(photo.getExifData().getDominantColor());

    }

    private String formDownloadUrl(String path, ImageService.Size size) {
        Path fileName = BASE_DIR.relativize(Paths.get(path));
        if ((!xxsEnabled && size == XXS)
                || (!xsEnabled && size == XS)
                || (!sEnabled && size == S)
                || (!mEnabled && size == M)
                || (!lEnabled && size == L)
                || (!xlEnabled && size == XL)) {
            //Returning original image if requested size disabled
            return (HOST + IMG_DOWNLOAD_SERVICE + "/" + RAW + "/" +
                    new String(Base64.getEncoder().encode(fileName.toString().getBytes(StandardCharsets.UTF_8))));
        } else {
            return (HOST + IMG_DOWNLOAD_SERVICE + "/" + size + "/" +
                    new String(Base64.getEncoder().encode(fileName.toString().getBytes(StandardCharsets.UTF_8))));
        }
    }

    private int calculateWidth(int currentWidth, int currentHeight, int fitToHeight) {
        float ratio = (float) currentHeight / (float) currentWidth;
        if (fitToHeight == currentWidth) {
            return currentWidth;
        }
        return Math.round((float) fitToHeight / ratio);
    }

    public Image getPreviewXXS() {
        return previewXXS;
    }

    public void setPreviewXXS(Image previewXXS) {
        this.previewXXS = previewXXS;
    }

    public Image getPreviewXS() {
        return previewXS;
    }

    public void setPreviewXS(Image previewXS) {
        this.previewXS = previewXS;
    }

    public Image getPreviewS() {
        return previewS;
    }

    public void setPreviewS(Image previewS) {
        this.previewS = previewS;
    }

    public Image getPreviewM() {
        return previewM;
    }

    public void setPreviewM(Image previewM) {
        this.previewM = previewM;
    }

    public Image getPreviewL() {
        return previewL;
    }

    public void setPreviewL(Image previewL) {
        this.previewL = previewL;
    }

    public Image getPreviewXL() {
        return previewXL;
    }

    public void setPreviewXL(Image previewXL) {
        this.previewXL = previewXL;
    }

    public Image getRaw() {
        return raw;
    }

    public void setRaw(Image raw) {
        this.raw = raw;
    }

    public String getDominantColor() {
        return dominantColor;
    }

    public void setDominantColor(String dominantColor) {
        this.dominantColor = dominantColor;
    }
}
