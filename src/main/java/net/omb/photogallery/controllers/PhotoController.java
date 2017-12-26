package net.omb.photogallery.controllers;

import net.omb.photogallery.services.DiskService;
import net.omb.photogallery.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Base64;

/**
 * Created by volodymyr.bodnar on 8/18/2017.
 */
@Controller
public class PhotoController {

    @Autowired
    private DiskService diskService;

    @Autowired
    private ImageService imageService;

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

//    @RequestMapping(value = {"/testService", "/testService/"}, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<String> testService (HttpServletRequest request, @RequestParam Boolean throwException) throws Exception {
//        if(throwException){
//            throw new Exception("Alert! Exception");
//        }else {
//            return ResponseEntity.ok("Successfully authorized, no exceptions were thrown");
//        }
//    }

    @RequestMapping(value = {"/testService/"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getGalleryJson () throws Exception {
        return ResponseEntity.ok("hi");
    }

    @RequestMapping(value = {"/getGalleryJson/{directory}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getGalleryJson (@PathVariable("directory") String directory) throws Exception {
        return ResponseEntity.ok(diskService.getGalleryJson(directory));
    }

    @RequestMapping(value = {"/getImage/{size}/{path}"}, method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getGalleryJson (@PathVariable("size") ImageService.Size size, @PathVariable("path") String path) throws Exception {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header("content-disposition", "inline;filename=" + new File(new String(Base64.getDecoder().decode(path))).getName())
                .body(new InputStreamResource(new ByteArrayInputStream(imageService.getImage(new String(Base64.getDecoder().decode(path)), size))));
    }
}
