package net.omb.photogallery.services;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.file.FileSystemDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import net.omb.photogallery.annotations.LogExecutionTime;
import net.omb.photogallery.exceptions.FileReadException;
import net.omb.photogallery.model.ExifData;
import net.omb.photogallery.model.Photo;
import net.omb.photogallery.model.Tag;
import net.omb.photogallery.repositories.PhotoRepository;
import net.omb.photogallery.repositories.TagRepository;
import net.omb.photogallery.utils.colorthief.ColorThief;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.drew.metadata.file.FileSystemDirectory.TAG_FILE_SIZE;
import static net.omb.photogallery.services.ImageService.Size.*;
import static org.imgscalr.Scalr.Rotation.*;

/**
 * Created by volodymyr.bodnar on 9/17/2017.
 */

@Service
@LogExecutionTime
public class ImageService {
    protected static final Logger log = LoggerFactory.getLogger(ImageService.class);
    private static final List<String> imagesExtensions = new ArrayList<>();

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

    @Autowired
    private PhotoService photoService;

    @Autowired
    private TagService tagService;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private AuthenticationManager authenticationManager;


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
        createPreviewFoldersStructure(imagesRootFolder);
    }

    private void createPreviewFoldersStructure(Path folder) {
        log.info("Creating preview folders structure");
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(folder)) {
            for (Path previewFolder : previewFolders) {
                if (folder.equals(Paths.get(imagesFolder)))
                    continue; //skipping creation of preview folders in root folder
                if (!xxsEnabled && previewFolder.getFileName().toString().equals(Size.XXS.name())) continue;
                if (!xsEnabled && previewFolder.getFileName().toString().equals(Size.XS.name())) continue;
                if (!sEnabled && previewFolder.getFileName().toString().equals(Size.S.name())) continue;
                if (!mEnabled && previewFolder.getFileName().toString().equals(Size.M.name())) continue;
                if (!lEnabled && previewFolder.getFileName().toString().equals(Size.L.name())) continue;
                if (!xlEnabled && previewFolder.getFileName().toString().equals(Size.XL.name())) continue;

                Path createFolder = Paths.get(folder + File.separator + previewFolder);
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

    public byte[] getImage(String path, Size size) {
        String format = getFormat(Paths.get(path));

        if (size == RAW) {
            log.debug("getImage: " + imagesFolder + File.separator + path);
            return imageToArray(Paths.get(imagesFolder + File.separator + path), format);
        } else {
            log.debug(imagesFolder + File.separator + Paths.get(path).getParent() + File.separator + size + File.separator + Paths.get(path).getFileName());
            return imageToArray(Paths.get(imagesFolder + File.separator + Paths.get(path).getParent() + File.separator + size + File.separator + Paths.get(path).getFileName()), format);
        }
    }

    private byte[] imageToArray(Path path, String format) {
        try {
            BufferedImage image = ImageIO.read(path.toFile());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, format, baos);
            baos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FileReadException(e);
        }
    }

    private boolean isOneOfPreviewFolders(Path folder) throws IOException {
        for (Path previewFolder : previewFolders) {
            if (folder.getFileName().equals(previewFolder)) {
                return true;
            }
        }
        return false;
    }

    private boolean createAllSizes(Path originalFile) {
        boolean atLeastOneThumbnailCreated = false;
        try {
            String format = getFormat(originalFile);

            Path imageFolder = originalFile.getParent();
            if (!Files.isDirectory(imageFolder) || !Files.exists(imageFolder)) {
                log.error("Cant find folder with image: " + originalFile);
                return atLeastOneThumbnailCreated;
            }

            for (Size size : Size.values()) {
                if (size == Size.RAW) continue;
                if (!xxsEnabled && size == Size.XXS) continue;
                if (!xsEnabled && size == Size.XS) continue;
                if (!sEnabled && size == Size.S) continue;
                if (!mEnabled && size == Size.M) continue;
                if (!lEnabled && size == Size.L) continue;
                if (!xlEnabled && size == Size.XL) continue;

                File resizedImageFile = new File(imageFolder + File.separator + size.name() + File.separator + originalFile.getFileName());
                if (resizedImageFile.exists()) continue;

                int orientation = 0;
                Metadata metadata = ImageMetadataReader.readMetadata(originalFile.toFile());
                ExifIFD0Directory exifIfInfo = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
                if (exifIfInfo != null) {
                    try {
                        orientation = exifIfInfo.getInt(ExifDirectoryBase.TAG_ORIENTATION);
                    } catch (MetadataException e) {
                        log.error("Can't get orientation size", e);
                    }
                }

                BufferedImage originalImage = ImageIO.read(originalFile.toFile());
                BufferedImage resizedImage;
                switch (orientation) {
                    case 3:
                        resizedImage =
                                Scalr.resize(originalImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_HEIGHT, size.getHeight(), Scalr.OP_ANTIALIAS);
                        resizedImage = Scalr.rotate(resizedImage, CW_180);
                        break;
                    case 6:
                        resizedImage =
                                Scalr.resize(originalImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, size.getHeight(), Scalr.OP_ANTIALIAS);
                        resizedImage = Scalr.rotate(resizedImage, CW_90);
                        break;
                    case 8:
                        resizedImage =
                                Scalr.resize(originalImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, size.getHeight(), Scalr.OP_ANTIALIAS);
                        resizedImage = Scalr.rotate(resizedImage, CW_270);
                        break;
                    default:
                        resizedImage =
                                Scalr.resize(originalImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_HEIGHT, size.getHeight(), Scalr.OP_ANTIALIAS);
                        break;
                }
                ImageIO.write(resizedImage, format, resizedImageFile);

                atLeastOneThumbnailCreated = true;

                if (log.isDebugEnabled()) log.debug("Created image for size:" + size + ", " +
                        "width: " + resizedImage.getWidth() + ", height: " + size.height +
                        "original image: " + originalFile);
            }
        } catch (IOException | ImageProcessingException e) {
            log.error("Error resizing file: " + originalFile, e);
            return atLeastOneThumbnailCreated;
        }
        return atLeastOneThumbnailCreated;
    }

    public void addImageInfoToDb(Path originalFile, List<Tag> tags) {
        log.info("Saving image info to db " + originalFile);
        originalFile = originalFile.toAbsolutePath().normalize();
        if (photoRepository.findOneByPath(originalFile.toString()).isPresent()) {
            log.debug("Information about image already saved to db");
            return;
        }
        Photo photo = new Photo();
        photo.setTags(tags);
        photo.setExifData(getExifData(originalFile));
        photo.setSize(originalFile.toFile().length());
        photo.setPath(originalFile.toString());
        photo.setExtension(FilenameUtils.getExtension(originalFile.toString()));
        photo.setName(FilenameUtils.getBaseName(originalFile.toString()));

        photoService.createIfNotExist(photo);

        log.debug("New entry has been added to db");

    }

    public void createGallery(String path, List<Tag> tags, List<MultipartFile> files) throws IOException {
        Path galleryFolder = Paths.get(imagesFolder + File.separator + path.replace("\\", File.separator).replace("/", File.separator));
        log.debug("Gallery path: " + galleryFolder);
        if (galleryFolder.equals(imagesFolder)) {
            throw new RuntimeException("Can't upload images to root folder");
        }
        if (!Files.exists(galleryFolder)) {
            Files.createDirectories(galleryFolder);
            createPreviewFoldersStructure(galleryFolder);
        }
        tags = tagService.findAndSaveIfNotExist(tags.stream().map(tag -> tag.getName()).collect(Collectors.toList()));

        for (MultipartFile file : files) {
            Path imageFile = galleryFolder.resolve(Paths.get(file.getOriginalFilename()));
            file.transferTo(imageFile.toFile());
            createAllSizes(imageFile);
            addImageInfoToDb(imageFile, tags);
        }
    }

    public void addPhoto(String path, List<Tag> tags, List<MultipartFile> files) throws IOException {
        Path galleryFolder = Paths.get(imagesFolder + File.separator + path.replace("\\", File.separator).replace("/", File.separator));
        if (!Files.exists(galleryFolder)) {
            throw new RuntimeException("Gallery folder does not exist");
        }
        tags = tagService.findAndSaveIfNotExist(tags.stream().map(tag -> tag.getName()).collect(Collectors.toList()));

        for (MultipartFile file : files) {
            Path imageFile = galleryFolder.resolve(Paths.get(file.getOriginalFilename()));
            file.transferTo(imageFile.toFile());
            createAllSizes(imageFile);
            addImageInfoToDb(imageFile, tags);
        }
    }

    private ExifData getExifData(Path originalFile) {
        ExifData exifData = new ExifData();

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(originalFile.toFile());

            ExifIFD0Directory exifIfInfo = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (exifIfInfo != null) {
                exifData.setCameraModel(exifIfInfo.getString(ExifDirectoryBase.TAG_MODEL, StandardCharsets.UTF_8.name()));
                if(exifIfInfo.getDate(ExifDirectoryBase.TAG_DATETIME) != null) {
                    exifData.setRecordedDate(exifIfInfo.getDate(ExifDirectoryBase.TAG_DATETIME).getTime());
                }
                try {
                    exifData.setOrientation(exifIfInfo.getInt(ExifDirectoryBase.TAG_ORIENTATION));
                } catch (MetadataException e) {
                    log.error("Can't get orientation size", e);
                }
            }
            JpegDirectory jpgInfo = metadata.getFirstDirectoryOfType(JpegDirectory.class);
            if (jpgInfo != null) {
                try {
                    if (exifData.getOrientation() == 6 || exifData.getOrientation() == 8) {
                        exifData.setHeight(jpgInfo.getImageWidth()); //width will be height after rotation
                        exifData.setWidth(jpgInfo.getImageHeight());
                    } else {
                        exifData.setHeight(jpgInfo.getImageHeight());
                        exifData.setWidth(jpgInfo.getImageWidth());
                    }
                } catch (MetadataException e) {
                    log.error("Can't get pixel size", e);
                }
            }

            BufferedImage originalImage = ImageIO.read(originalFile.toFile());
            exifData.setDominantColor(ColorThief.getColorAsHash(originalImage));

        } catch (IOException | ImageProcessingException e) {
            throw new RuntimeException("Could not read Image data", e);
        }

        return exifData;
    }

    public String getFormat(Path file) {
        try {
            String format = Files.probeContentType(file);
            log.debug("Format of file: " + file + " - " + format);
            for (String imageFormat : ImageIO.getWriterFormatNames()) {
                if ( format != null && format.contains(imageFormat)) return imageFormat;
            }
        } catch (IOException e) {
            log.error("Error getting file format from " + file, e);
            return "jpeg";
        }

        return "jpeg";
    }


    public void scanFolderSync() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("admin", "admin");
        Authentication authentication = this.authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        scanFolder(Paths.get(imagesFolder));
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private int scanFolder(Path scanningFolder) {
        if (!Files.exists(scanningFolder)) {
            log.error("Folder: " + scanningFolder + " does not exist");
        }
        int resizedImagesCount = 0;

        log.info("Scanning " + scanningFolder + " folder and resizing images");
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(scanningFolder)) {
            String folderName = scanningFolder.getFileName().toString().replaceAll("[\\p{Punct}\\d]", "").trim().toLowerCase();
            List<Tag> tags = tagService.findAndSaveIfNotExist(new ArrayList<>(Arrays.asList(folderName))); //this tag will be applied for all folder children
            log.info("'" + folderName + "' tag will be added for all images in this folder");
            for (Path child : ds) {
                if (isImage(child)) {
                    log.info("Creating resized copies for: " + child);

                    if (createAllSizes(child)) {
                        resizedImagesCount++;
                    }
                    addImageInfoToDb(child, tags);
                } else if (Files.isDirectory(child) &&
                        //don't scan directories with thumbnails
                        !isOneOfPreviewFolders(child)) {
                    resizedImagesCount += scanFolder(child);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        log.info("Resized " + resizedImagesCount + " images");
        return resizedImagesCount;
    }

    private boolean isImage(Path path) {
        String extension = FilenameUtils.getExtension(path.getFileName().toString());
        return Files.isRegularFile(path) && imagesExtensions.contains(extension.toLowerCase());
    }

    public enum Size {
        XXS(300), XS(600), S(768), M(1080), L(1440), XL(2160), RAW(-1);

        private int height;

        Size(int height) {
            this.height = height;
        }

        public int getHeight() {
            return height;
        }
    }

    public ByteArrayResource pack(String sourceDirPath) throws IOException {
        Path pp = Paths.get(imagesFolder + File.separator + sourceDirPath);
        if (!Files.exists(pp) || !Files.isDirectory(pp)) {
            throw new RuntimeException("Can't find gallery folder");
        }
        Path p = Files.createTempFile(pp.getFileName().toString(), "");
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {

            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path) && !previewFolders.contains(path.getParent().getFileName()))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            System.err.println(e);
                        }
                    });
        }
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(p)) {
            @Override
            public String getFilename() {
                return pp.getFileName().toString() + ".zip";
            }
        };
        return resource;
    }

}

