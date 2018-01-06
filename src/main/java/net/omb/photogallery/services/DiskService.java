package net.omb.photogallery.services;

import net.omb.photogallery.model.json.Folder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
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

    @Value("${root.gallery.dir}")
    private String rootDirectory;


    public Folder getFolders(String folder) {
        Folder newFolder;
        Path path;
        if (folder == null) {
            path = Paths.get(rootDirectory);
            newFolder = new Folder();
            newFolder.setName(File.separator);
            newFolder.setPath(File.separator);
        } else {
            path = Paths.get(folder);
            newFolder = new Folder();
            newFolder.setName(path.getFileName().toString());
            newFolder.setPath(Paths.get(rootDirectory).relativize(path).toString());
        }

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
            for (Path pathChild : ds) {
                if (Files.isDirectory(pathChild)) {
                    if (pathChild.getFileName().toString().equals(ImageService.Size.XXS.name())) continue;
                    if (pathChild.getFileName().toString().equals(ImageService.Size.XS.name())) continue;
                    if (pathChild.getFileName().toString().equals(ImageService.Size.S.name())) continue;
                    if (pathChild.getFileName().toString().equals(ImageService.Size.M.name())) continue;
                    if (pathChild.getFileName().toString().equals(ImageService.Size.L.name())) continue;
                    if (pathChild.getFileName().toString().equals(ImageService.Size.XL.name())) continue;
                    Folder child = getFolders(pathChild.toString());
                    newFolder.addChild(child);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return newFolder;
    }

}
