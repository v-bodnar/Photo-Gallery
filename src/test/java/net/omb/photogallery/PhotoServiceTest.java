package net.omb.photogallery;

import net.omb.photogallery.model.ExifData;
import net.omb.photogallery.model.Photo;
import net.omb.photogallery.model.Tag;
import net.omb.photogallery.repositories.ExifDataRepository;
import net.omb.photogallery.services.PhotoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;

@WithMockUser(username = "bodik@list.ru")
@RunWith(SpringRunner.class)
@SpringBootTest
public class PhotoServiceTest {


    @Autowired
    private PhotoService photoService;

    @Autowired
    private ExifDataRepository exifDataRepository;

    @Test
    //@Rollback(value = false)
    public void createIfNotExist() throws InterruptedException {
        ExifData exifData = new ExifData();
        exifData.setHeight(637);
        exifData.setWidth(960);
        //exifDataRepository.save(exifData);

        List<Tag> tags = Arrays.asList(new Tag("nature"), new Tag("insect"), new Tag("macro"));

        Photo photo = new Photo();
        photo.setPath("d:\\Work\\Projects\\temp\\gallery\\first\\ladybug-796481_960_720.jpg");
        photo.setName("ladybug");
        photo.setExtension("jpg");
        photo.setExifData(exifData);
        photo.setTags(tags);
        Photo createdPhoto = photoService.createIfNotExist(photo);
        assertNotNull(createdPhoto);
    }

    @Test
    public void createIfNotExistList() throws InterruptedException {
        ExifData exifData = new ExifData();
        exifData.setHeight(637);
        exifData.setWidth(960);

        List<Tag> tags = Arrays.asList(new Tag("nature"), new Tag("insect"), new Tag("macro"));

        Photo photo = new Photo();
        photo.setPath("d:\\Work\\Projects\\temp\\gallery\\first\\ladybug-796481_960_720.jpg");
        photo.setName("ladybug");
        photo.setExtension("jpg");
        photo.setExifData(exifData);
        photo.setTags(tags);

        ExifData exifData2 = new ExifData();
        exifData.setHeight(2560);
        exifData.setWidth(1600);

        List<Tag> tags2 = Arrays.asList(new Tag("nature"), new Tag("forest"));

        Photo photo2 = new Photo();
        photo2.setPath("d:\\Work\\Projects\\temp\\gallery\\first\\pexels-photo.jpg");
        photo2.setName("ladybug");
        photo2.setExtension("jpg");
        photo2.setExifData(exifData2);
        photo2.setTags(tags2);
        List<Photo> result = photoService.createIfNotExist(Arrays.asList(photo, photo2));
        assertNotNull(result);
    }
}
