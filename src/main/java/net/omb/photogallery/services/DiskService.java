package net.omb.photogallery.services;

import net.omb.photogallery.exceptions.FileReadException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by volodymyr.bodnar on 9/16/2017.
 */
@Service
public class DiskService {

    private static Logger log = LoggerFactory.getLogger(DiskService.class);
    private static final String jsonFileName = "data.json";

    @Value("root.gallery.dir")
    private String rootDirectory;

    public String getGalleryJson(String directory){
        Path path = Paths.get(rootDirectory + directory);
        if(!Files.exists(path)){
            throw new FileReadException("Directory does not exist");
        }
        if(!Files.isDirectory(path)){
            throw new FileReadException("Given path is not a gallery directory");
        }
        path = Paths.get(path.normalize().toAbsolutePath().toString(), jsonFileName);
        if(!Files.isReadable(path)){
            throw new FileReadException("Can't access json, it is not readable");
        }

        try {
            return FileUtils.readFileToString(path.toFile(), "UTF-8");
        } catch (IOException e) {
            throw new FileReadException(e.getMessage(), e);
        }
    }

    public Path getImage(String imagePath){
        Path path = Paths.get(rootDirectory + imagePath);
        if(!Files.exists(path)){
            throw new FileReadException("Image does not exist");
        }
        if(!Files.isReadable(path)){
            throw new FileReadException("Can't access image, it is not readable");
        }
        return path;
    }


}
