package net.omb.photogallery.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.omb.photogallery.annotations.LogExecutionTime;
import net.omb.photogallery.model.Photo;
import net.omb.photogallery.model.Tag;
import net.omb.photogallery.model.json.Folder;
import net.omb.photogallery.model.json.Preview;
import net.omb.photogallery.repositories.TagRepository;
import net.omb.photogallery.services.DiskService;
import net.omb.photogallery.services.ImageService;
import net.omb.photogallery.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by volodymyr.bodnar on 8/18/2017.
 */
@Controller
@LogExecutionTime
@RequestMapping(value = "api")
public class PhotoController {

    @Autowired
    private DiskService diskService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private TagRepository tagRepository;

    private static final ObjectMapper jacksonMapper = new ObjectMapper();

    @RequestMapping(value = {"/findByDirectoryAndTagsLikeAndDateBetween/{directory}/{tags}/{dateFrom}/{dateTo}", "/findByDirectoryAndTagsLikeAndDateBetween/{directory}/{tags}/{dateFrom}/{dateTo}/"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> findByDirectoryAndTagsLikeAndDateBetween(
            @PathVariable("directory") String directory,
            @PathVariable("tags") List<String> tags,
            @PathVariable("dateFrom") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFrom,
            @PathVariable("dateTo") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo) throws Exception {
        return responseFromPhotoList(photoService.findByDirectoryAndTagsLikeAndDateBetween(new String(Base64.getDecoder().decode(directory.getBytes(StandardCharsets.UTF_8))), tags, dateFrom, dateTo, false));
    }

    @RequestMapping(value = {"/findByDirectoryAndTagsLike/{directory}/{tags}", "/findByDirectoryAndTagsLike/{directory}/{tags}/"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> findByDirectoryAndTagsLike(
            @PathVariable("directory") String directory,
            @PathVariable("tags") List<String> tags) throws Exception {
        return responseFromPhotoList(photoService.findByDirectoryAndTagsLike(new String(Base64.getDecoder().decode(directory.getBytes(StandardCharsets.UTF_8))), tags, false));
    }

    @RequestMapping(value = {"/findByDirectoryAndDateBetween/{directory}/{dateFrom}/{dateTo}", "/findByDirectoryAndDateBetween/{directory}/{dateFrom}/{dateTo}/"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> findByDirectoryAndDateBetween(
            @PathVariable("directory") String directory,
            @PathVariable("dateFrom") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFrom,
            @PathVariable("dateTo") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo) throws Exception {
        return responseFromPhotoList(photoService.findByDirectoryAndDateBetween(new String(Base64.getDecoder().decode(directory.getBytes(StandardCharsets.UTF_8))), dateFrom, dateTo, false));
    }

    @RequestMapping(value = {"/findByTagsLikeAndDateBetween/{tags}/{dateFrom}/{dateTo}", "/findByTagsLikeAndDateBetween/{tags}/{dateFrom}/{dateTo}/"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> findByTagsLikeAndDateBetween(
            @PathVariable("tags") List<String> tags,
            @PathVariable("dateFrom") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFrom,
            @PathVariable("dateTo") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo) throws Exception {
        return responseFromPhotoList(photoService.findByTagsLikeAndDateBetween(tags, dateFrom, dateTo));
    }

    @RequestMapping(value = {"/findByTagsLike/{tags}", "/findByTagsLike/{tags}/"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> findByTagsLike(
            @PathVariable("tags") List<String> tags) throws Exception {
        return responseFromPhotoList(photoService.findByTagsLike(tags));
    }

    @RequestMapping(value = {"/findByDateBetween/{dateFrom}/{dateTo}", "/findByDateBetween/{dateFrom}/{dateTo}/"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> findByDateBetween(
            @PathVariable("dateFrom") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFrom,
            @PathVariable("dateTo") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo) throws Exception {
        return responseFromPhotoList(photoService.findByDateBetween(dateFrom, dateTo));
    }

    @RequestMapping(value = {   "/findByDirectory/{directory}", "/findByDirectory/{directory}/"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> findByDirectory(@PathVariable("directory") String directory) throws Exception {
        return responseFromPhotoList(photoService.findByDirectory(new String(Base64.getDecoder().decode(directory.getBytes(StandardCharsets.UTF_8))), false));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = {"/uploadPhotos", "/uploadPhotos/"}, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> uploadPhotos(@RequestParam List<MultipartFile> files,
                                             @RequestParam String tags,
                                             @RequestParam String path) throws Exception {
        List<Tag> tagsList = jacksonMapper.readValue(tags, new TypeReference<List<Tag>>() {});
        imageService.createGallery(path, tagsList, files);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = {"/downloadGallery/{galleryPath}","/downloadGallery/{galleryPath}/"}, method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadGallery(@PathVariable("galleryPath") String galleryPath) throws IOException {
        Path path = Paths.get(new String(Base64.getDecoder().decode(galleryPath.getBytes(StandardCharsets.UTF_8))));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add("Content-Disposition", "attachment; filename=" + path.getFileName() + ".zip");

        ByteArrayResource resource = imageService.pack(path.toString());
        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .headers(headers)
                .body(resource);
    }

    private ResponseEntity<String> responseFromPhotoList(List<Photo> photos) throws JsonProcessingException {
        if (photos == null || photos.isEmpty()) {
            return ResponseEntity.ok("[]");
        } else {
            List<Preview> photoListForJson = photos.stream().map(photo -> new Preview(photo)).collect(Collectors.toList());
            String json = jacksonMapper.writeValueAsString(photoListForJson);
            return ResponseEntity.ok(json);
        }
    }

    @RequestMapping(value = {"/getImage/{size}/{path}"}, method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getImage(@PathVariable("size") ImageService.Size size, @PathVariable("path") String path) throws Exception {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/" +imageService.getFormat(Paths.get(new String(Base64.getDecoder().decode(path))))))
                .header("content-disposition", "inline;filename=" + new File(new String(Base64.getDecoder().decode(path))).getName())
                .body(new InputStreamResource(new ByteArrayInputStream(imageService.getImage(new String(Base64.getDecoder().decode(path)), size))));
    }

    @RequestMapping(value = {"/getFolders/", "/getFolders"}, method = RequestMethod.GET)
    public ResponseEntity<Folder> getFolders() throws Exception {
        return ResponseEntity.ok(diskService.getFolders(null));
    }

    @RequestMapping(value = {"/getTags/", "/getTags"}, method = RequestMethod.GET)
    public ResponseEntity<List<Tag>> getTags() throws Exception {
        return ResponseEntity.ok((List<Tag>) tagRepository.findAll());
    }
}
