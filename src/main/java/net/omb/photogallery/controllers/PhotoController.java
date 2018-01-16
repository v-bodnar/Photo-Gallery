package net.omb.photogallery.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.omb.photogallery.model.Photo;
import net.omb.photogallery.model.json.Folder;
import net.omb.photogallery.model.json.Preview;
import net.omb.photogallery.services.DiskService;
import net.omb.photogallery.services.ImageService;
import net.omb.photogallery.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by volodymyr.bodnar on 8/18/2017.
 */
@Controller
@RequestMapping(value = "api")
public class PhotoController {

    @Autowired
    private DiskService diskService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private PhotoService photoService;

    private static final ObjectMapper jacksonMapper = new ObjectMapper();

//    @RequestMapping(value = "/upload", method= RequestMethod.POST, headers = "Content-Type=multipart/form-data")
//    @ResponseStatus(value = HttpStatus.OK)
//    public void uploadFile(@RequestParam("database") MultipartFile databaseFile){
//        String filePath = "c:\\Vova\\" + databaseFile.getOriginalFilename();
//
//        try {
//            FileUtils.copyInputStreamToFile(databaseFile.getInputStream(), new File(filePath));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @RequestMapping(value = {"/getGalleryByDirectory/{directory}","/getGalleryByDirectory/{directory}/" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getGalleryByDirectory (@PathVariable("directory") String directory) throws Exception {
        List<Photo> photos = photoService.findByDirectory(new String(Base64.getDecoder().decode(directory.getBytes(StandardCharsets.UTF_8))), false);
        if(photos == null || photos.isEmpty()){
             return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body("There is no images in folder: " + directory + " or they are not indexed");
        }else {
            List<Preview> photoListForJson = photos.stream().map(photo -> new Preview(photo)).collect(Collectors.toList());
            String json = jacksonMapper.writeValueAsString(photoListForJson);
            return ResponseEntity.ok(json);
        }
    }

    @RequestMapping(value = {"/getGalleryByDirectory/{directory}/{tags}","/getGalleryByDirectory/{directory}/{tags}/" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getGalleryByDirectoryAndTags (@PathVariable("directory") String directory, @PathVariable("tags") List<String> tags) throws Exception {
        List<Photo> photos = photoService.findByDirectory(new String(Base64.getDecoder().decode(directory.getBytes(StandardCharsets.UTF_8))), false);
        if(photos == null || photos.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body("There is no images in folder: " + directory + " or they are not indexed");
        }else {
            List<Preview> photoListForJson = photos.stream().map(photo -> new Preview(photo)).collect(Collectors.toList());
            String json = jacksonMapper.writeValueAsString(photoListForJson);
            return ResponseEntity.ok(json);
        }
    }

    @RequestMapping(value = {"/getImage/{size}/{path}"}, method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getImage (@PathVariable("size") ImageService.Size size, @PathVariable("path") String path) throws Exception {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header("content-disposition", "inline;filename=" + new File(new String(Base64.getDecoder().decode(path))).getName())
                .body(new InputStreamResource(new ByteArrayInputStream(imageService.getImage(new String(Base64.getDecoder().decode(path)), size))));
    }

    @RequestMapping(value = {"/getFolders/", "/getFolders"}, method = RequestMethod.GET)
    public ResponseEntity<Folder> getFolders () throws Exception {
        return ResponseEntity.ok(diskService.getFolders(null));
    }
}
