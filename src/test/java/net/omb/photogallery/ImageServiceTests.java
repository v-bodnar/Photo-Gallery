package net.omb.photogallery;

import net.omb.photogallery.services.ImageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Paths;

/**
 * Created by volodymyr.bodnar on 10/11/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageServiceTests {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ImageService imageService;

    @Value("${root.gallery.dir}")
    private String imagesFolder;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).dispatchOptions(true).build();
    }

    @Test
    public void scanFolder() throws InterruptedException {
        imageService.scanFolderSync();
    }
}
