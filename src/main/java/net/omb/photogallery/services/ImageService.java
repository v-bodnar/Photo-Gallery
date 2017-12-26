package net.omb.photogallery.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.omb.photogallery.exceptions.FileReadException;
import net.omb.photogallery.model.json.Image;
import net.omb.photogallery.model.json.Preview;
import net.omb.photogallery.utils.colorthief.ColorThief;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.awt.image.MultiResolutionImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static net.omb.photogallery.services.ImageService.Size.*;

/**
 * Created by volodymyr.bodnar on 9/17/2017.
 */

@Service
public class ImageService {
    protected static final Logger log = LoggerFactory.getLogger(ImageService.class);
    private static final String jsonFileName = "data.json";
    private static final ObjectMapper jacksonMapper = new ObjectMapper();
    private static final List<String> imagesExtensions = new ArrayList<>();
    private static final String GET_IMAGE_SERVICE = "getImage";

    static {
        imagesExtensions.add("jpg");
        imagesExtensions.add("jpeg");
        imagesExtensions.add("bmp");
        imagesExtensions.add("gif");
        imagesExtensions.add("png");
    }
    @Value("${root.gallery.dir}")
    private String imagesFolder;

    @Value("${root.context.path}")
    private String rootPath;

    @Value("${size.xxs.enabled:false}")
    private boolean xxsEnabled;

    @Value("${size.xs.enabled:false}")
    private boolean xsEnabled;

    @Value("${size.s.enabled:false}")
    private boolean sEnabled;

    @Value("${size.m.enabled:false}")
    private boolean mEnabled;

    @Value("${size.l.enabled:false}")
    private boolean lEnabled;

    @Value("${size.xl.enabled:false}")
    private boolean xlEnabled;
//
//    @Value("${size.xxs.height}")
//    private int heightXXS;
//
//    @Value("${size.xs.height}")
//    private int heightXS;
//
//    @Value("${size.s.height}")
//    private int heightS;
//
//    @Value("${size.m.height}")
//    private int heightM;
//
//    @Value("${size.l.height}")
//    private int heightL;
//
//    @Value("${size.xl.height}")
//    private int heightXL;

    private java.util.List<Path> previewFolders = new LinkedList<>();

    @PostConstruct
    public void init() {
        Path imagesRootFolder = Paths.get(imagesFolder);

        try {
            //Checking if directories exist, create if not
            if (!Files.exists(imagesRootFolder)) {
                log.info("Root folder for gallery will be created " + imagesRootFolder);
                Files.createDirectory(imagesRootFolder);
            }
        } catch (IOException e) {
            log.error("Could not create directory for images", e);
        }

        previewFolders.add(Paths.get(XXS.name()));
        previewFolders.add(Paths.get(XS.name()));
        previewFolders.add(Paths.get(S.name()));
        previewFolders.add(Paths.get(M.name()));
        previewFolders.add(Paths.get(Size.L.name()));
        previewFolders.add(Paths.get(Size.XL.name()));

       // generateGalleryJson(imagesRootFolder);
        createPreviewFoldersStructure(imagesRootFolder);

        //jacksonMapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
        //jacksonMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);

    }

    private void createPreviewFoldersStructure(Path folder) {
        log.info("Creating preview folders structure");
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(folder)){
            for (Path previewFolder : previewFolders) {
                if(folder.equals(Paths.get(imagesFolder))) continue; //skipping creation of preview folders in root folder
                if(!xxsEnabled && previewFolder.getFileName().toString().equals(Size.XXS.name())) continue;
                if(!xsEnabled && previewFolder.getFileName().toString().equals(Size.XS.name())) continue;
                if(!sEnabled && previewFolder.getFileName().toString().equals(Size.S.name())) continue;
                if(!mEnabled && previewFolder.getFileName().toString().equals(Size.M.name())) continue;
                if(!lEnabled && previewFolder.getFileName().toString().equals(Size.L.name())) continue;
                if(!xlEnabled && previewFolder.getFileName().toString().equals(Size.XL.name())) continue;

                Path createFolder  = Paths.get(folder + File.separator + previewFolder);
                if (!Files.exists(createFolder) && !Paths.get(imagesFolder).equals(folder)) {
                    log.debug("Folder " + previewFolder + " will be created");
                    Files.createDirectory(createFolder);
                }
            }
            for (Path child : ds) {
                if (Files.isDirectory(child) &&
                        //excluding directories with already resized images
                        !isOneOfPreviewFolders(child)) {
                    createPreviewFoldersStructure(child.toAbsolutePath());
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

//    public void generateGalleryJson(Path directory){
//        if(!Files.exists(directory)){
//            throw new FileReadException("Directory does not exist");
//        }
//        if(!Files.isDirectory(directory)){
//            throw new FileReadException("Given path is not a gallery directory");
//        }
//
//
//        log.info("Generating json gallery file");
//        List<Preview> entityListForJson = new LinkedList<>();
//        try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory)) {
//            for (Path child : ds) {
//                if (isImage(child)){
//                    Preview entityForJson = new Preview();
//                    BufferedImage originalImage = ImageIO.read(child.toFile());
//                    entityForJson.setRaw(pathToImage(child));
//                    entityForJson.setPreviewXXS(pathToImage(Paths.get(directory + File.separator + XXS + File.separator + child.getFileName())));
//                    entityForJson.setPreviewXS(pathToImage(Paths.get(directory + File.separator + XS + File.separator + child.getFileName())));
//                    entityForJson.setPreviewS(pathToImage(Paths.get(directory + File.separator + S + File.separator + child.getFileName())));
//                    entityForJson.setPreviewM(pathToImage(Paths.get(directory + File.separator + M + File.separator + child.getFileName())));
//                    entityForJson.setPreviewL(pathToImage(Paths.get(directory + File.separator + Size.L + File.separator + child.getFileName())));
//                    entityForJson.setPreviewXL(pathToImage(Paths.get(directory + File.separator + Size.XL + File.separator + child.getFileName())));
//                    entityForJson.setDominantColor(ColorThief.getColorAsHash(originalImage));
//                    entityListForJson.add(entityForJson);
//                }
//            }
//
//            Path jsonGalleryFile = Paths.get(directory + File.separator + jsonFileName);
//            if(!Files.exists(jsonGalleryFile)){
//                log.info("Creating gallery file: " + jsonGalleryFile);
//            }
//            String json = jacksonMapper.writeValueAsString(entityListForJson);
//            FileUtils.writeStringToFile(jsonGalleryFile.toFile(), json, StandardCharsets.UTF_8);
//
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        }
//    }

//    public void generateGalleryJson(Path directory){
//        if(!Files.exists(directory)){
//            throw new FileReadException("Directory does not exist");
//        }
//        if(!Files.isDirectory(directory)){
//            throw new FileReadException("Given path is not a gallery directory");
//        }
//
//        log.info("Generating json gallery file");
//        List<Preview> entityListForJson = new LinkedList<>();
//
//        Path galleryFolder = Paths.get(imagesFolder);
//        try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory)) {
//            for (Path child : ds) {
//                if (isImage(child)){
//                    Preview entityForJson = new Preview();
//                    String url = new String(Base64.encodeBase64(galleryFolder.relativize(child).toString().getBytes(StandardCharsets.UTF_8)));
//                    BufferedImage originalImage = ImageIO.read(child.toFile());
//                    entityForJson.setRaw(new Image(rootPath + GET_IMAGE_SERVICE + "/" + RAW.name() + url, getImageWidth(child, XXS), XXS.height)); //todo
//                    entityForJson.setPreviewXXS(new Image(rootPath + GET_IMAGE_SERVICE + "/" + XXS.name() + "/" + url, getImageWidth(child, XXS), XXS.height));
//                    entityForJson.setPreviewXS(new Image(rootPath + GET_IMAGE_SERVICE + "/" + XS.name() + "/" + url, getImageWidth(child, XS), XS.height));
//                    entityForJson.setPreviewS(new Image(rootPath + GET_IMAGE_SERVICE + "/" + S.name() + "/" + url, getImageWidth(child, S), S.height));
//                    entityForJson.setPreviewM(new Image(rootPath + GET_IMAGE_SERVICE + "/" + M.name() + "/" + url, getImageWidth(child, M), M.height));
//                    entityForJson.setPreviewL(new Image(rootPath + GET_IMAGE_SERVICE + "/" + L.name() + "/" + url, getImageWidth(child, L), L.height));
//                    entityForJson.setPreviewXL(new Image(rootPath + GET_IMAGE_SERVICE + "/" + XL.name() + "/" + url, getImageWidth(child, XL), XL.height));
//                    entityForJson.setDominantColor(ColorThief.getColorAsHash(originalImage));
//                    entityListForJson.add(entityForJson);
//                }else if(Files.isDirectory(child)){
//                    generateGalleryJson(child);
//                }
//            }
//
//            Path jsonGalleryFile = Paths.get(directory + File.separator + jsonFileName);
//            if(!Files.exists(jsonGalleryFile)){
//                log.info("Creating gallery file: " + jsonGalleryFile);
//            }
//            String json = jacksonMapper.writeValueAsString(entityListForJson);
//            FileUtils.writeStringToFile(jsonGalleryFile.toFile(), json, StandardCharsets.UTF_8);
//
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        }
//
//    }
//
//    private int getImageWidth(Path path, Size size){
//        try {
//            final Iterator<ImageReader> readers = ImageIO.getImageReaders(path.toFile());
//            Double width = 0d;
//            if (readers.hasNext()) {
//                ImageReader reader = readers.next();
//                try {
//                    reader.setInput(path.toFile());
//                    width = (reader.getWidth(0) / ((double)reader.getHeight(0) / (double)size.getHeight()));
//                } finally {
//                    reader.dispose();
//                }
//            }
//
//            return width.intValue();
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//            throw new FileReadException(e);
//        }
//    }

    public byte[] getImage(String path, Size size) {
        path = imagesFolder + File.separator + path;
        BufferedImage image;
        try {
            image = ImageIO.read(Paths.get(path).toFile());

            MultiResolutionImage resoImages = new BaseMultiResolutionImage(image);
            if(size == RAW){
                return imageToArray(image, getFormat(Paths.get(path)));
            }

            int width = image.getWidth() / (image.getHeight() / size.getHeight());
            image = (BufferedImage)resoImages.getResolutionVariant((double) width, (double) size.getHeight());

            return imageToArray(image, getFormat(Paths.get(path)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FileReadException(e);
        }
    }

    private byte[] imageToArray(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image , format, baos);
        baos.flush();
        return baos.toByteArray();
    }

    private Image pathToImage(Path imagePath) throws IOException {
        if(!Files.exists(imagePath)){
            throw new FileReadException("Can't determine image or thumbnail size, file does not exist!");
        }
        BufferedImage bufferedImage = ImageIO.read(imagePath.toFile());
        Image image = new Image(imagePath.normalize().toString(), bufferedImage.getWidth(),bufferedImage.getHeight());
        return image;
    }

    private boolean isOneOfPreviewFolders(Path folder) throws IOException {
        for (Path previewFolder : previewFolders) {
            if(folder.getFileName().equals(previewFolder)){
                return true;
            }
        }
        return false;
    }

//    public byte[] getImage(String fileName, int width, int height) {
//
//        Path originalFile = Paths.get(imagesFolder + File.separator + fileName);
//        if (!Files.exists(originalFile)) {
//            return null;
//        }
//
//        Size size = determineSize(width, height);
//        if (size == Size.ORIGINAL) {
//            try {
//                if (log.isDebugEnabled())
//                    log.debug("Width or Height parameter from request are 0 returning original image");
//                return Files.readAllBytes(originalFile);
//            } catch (IOException e) {
//                log.error("Error reading file: " + originalFile, e);
//                return null;
//            }
//        }
//
//        Path resizedImage = Paths.get(imagesFolder + File.separator + size.name() + File.separator + fileName);
//
//        if (Files.exists(resizedImage)) {
//            try {
//                if (log.isDebugEnabled()) log.debug("Returning image: " + resizedImage);
//                return Files.readAllBytes(resizedImage);
//            } catch (IOException e) {
//                log.error("Error reading file: " + resizedImage, e);
//                return null;
//            }
//        } else {
//            if (log.isDebugEnabled()) log.debug("Resized image does not exist, trying to create and return");
//            return createResized(originalFile, size, getOrientation(width, height));
//        }
//    }


//    public byte[] getImage(String fileName, Size size) {
//
//        Path originalFile = Paths.get(imagesFolder + File.separator + fileName);
//        if (!Files.exists(originalFile)) {
//            return null;
//        }
//
//        Size size = determineSize(width, height);
//        if (size == Size.ORIGINAL) {
//            try {
//                if (log.isDebugEnabled())
//                    log.debug("Width or Height parameter from request are 0 returning original image");
//                return Files.readAllBytes(originalFile);
//            } catch (IOException e) {
//                log.error("Error reading file: " + originalFile, e);
//                return null;
//            }
//        }
//
//        Path resizedImage = Paths.get(imagesFolder + File.separator + size.name() + File.separator + fileName);
//
//        if (Files.exists(resizedImage)) {
//            try {
//                if (log.isDebugEnabled()) log.debug("Returning image: " + resizedImage);
//                return Files.readAllBytes(resizedImage);
//            } catch (IOException e) {
//                log.error("Error reading file: " + resizedImage, e);
//                return null;
//            }
//        } else {
//            if (log.isDebugEnabled()) log.debug("Resized image does not exist, trying to create and return");
//            return createResized(originalFile, size, getOrientation(width, height));
//        }
//    }

    private boolean createAllSizes(Path originalFile) {
        boolean atLeastOneThumbnailCreated = false;
        try {
            BufferedImage originalImage = ImageIO.read(originalFile.toFile());
            String format = getFormat(originalFile);

            Path imageFolder = originalFile.getParent();
            if (!Files.isDirectory(imageFolder) || !Files.exists(imageFolder)) {
                log.error("Cant find folder with image: " + originalFile);
                return atLeastOneThumbnailCreated;
            }

            for (Size size : Size.values()) {
                if(size == Size.RAW) continue;
                if(!xxsEnabled && size == Size.XXS) continue;
                if(!xsEnabled && size == Size.XS) continue;
                if(!sEnabled && size == Size.S) continue;
                if(!mEnabled && size == Size.M) continue;
                if(!lEnabled && size == Size.L) continue;
                if(!xlEnabled && size == Size.XL) continue;

                File resizedImageFile = new File(imageFolder + File.separator + size.name() + File.separator + originalFile.getFileName());
                if (resizedImageFile.exists()) continue;

                BufferedImage resizedImage =
                        Scalr.resize(originalImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_HEIGHT, size.getHeight(), Scalr.OP_ANTIALIAS);
                ImageIO.write(resizedImage, format, resizedImageFile);

                atLeastOneThumbnailCreated = true;

                if (log.isDebugEnabled()) log.debug("Created image for size:" + size + ", " +
                        "width: " + resizedImage.getWidth() + ", height: " + size.height +
                        "original image: " + originalFile);
            }
        } catch (IOException e) {
            log.error("Error resizing file: " + originalFile, e);
            return atLeastOneThumbnailCreated;
        }
        return atLeastOneThumbnailCreated;
    }

    private String getFormat(Path file) {
        try {
            String format = Files.probeContentType(file);

            for (String imageFormat : ImageIO.getWriterFormatNames()) {
                if (format.contains(imageFormat)) return imageFormat;
            }
        } catch (IOException e) {
            log.error("Error getting file format from " + file, e);
            return "";
        }

        return "";
    }

    private Orientation getOrientation(int width, int height) {
        if (width > height) {
            return Orientation.LANDSCAPE;
        } else {
            return Orientation.PORTRAIT;
        }
    }

    public void scanFolderSync() throws InterruptedException {
        Thread thread = new Thread(new BackgroundResizer());
        thread.start();
        thread.join();
    }

    private class BackgroundResizer implements Runnable {

        @Override
        public void run() {
            scanFolder(Paths.get(imagesFolder));
        }

        private int scanFolder(Path scanningFolder) {
            if (!Files.exists(scanningFolder)) {
                log.error("Folder: " + scanningFolder + " does not exist");
            }
            int resizedImagesCount = 0;

            log.info("Scanning " + scanningFolder + " folder and resizing images");
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(scanningFolder)) {
                for (Path child : ds) {
                    if (isImage(child)) {
                        log.info("Creating resized copies for: " + child);
                        if (createAllSizes(child)) {
                            resizedImagesCount++;
                        }
                    } else if (Files.isDirectory(child) &&
                            //don't scan directories with thumbnails
                            !isOneOfPreviewFolders(child)) {
                        resizedImagesCount += scanFolder(child);
                    }
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }

            //generateGalleryJson(scanningFolder);
            log.info("Resized " + resizedImagesCount + " images");
            return resizedImagesCount;
        }
    }

    private boolean isImage(Path path){
        String extension = FilenameUtils.getExtension(path.getFileName().toString());
        return Files.isRegularFile(path) && imagesExtensions.contains(extension.toLowerCase());
    }

    public enum Size {
        XXS(300), XS(600), S(768), M(1080), L(1440), XL(2160), RAW(-1);

        private int height;

        Size(int height){
            this.height = height;
        }

        public int getHeight() {
            return height;
        }
    }

    enum Orientation{
        PORTRAIT, LANDSCAPE;
    }
}

