package net.omb.photogallery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.omb.photogallery.model.json.Folder;
import net.omb.photogallery.services.DiskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertNotNull;

@WithMockUser(username = "bodik@list.ru")
@RunWith(SpringRunner.class)
@SpringBootTest
public class DiskServiceTest {
    @Autowired
    private DiskService diskService;

    @Value("${root.gallery.dir}")
    private String rootFolder;

    @Test
    public void getFolders() throws JsonProcessingException {
        Folder folder = diskService.getFolders(null);
        assertNotNull(folder);
        ObjectMapper jacksonMapper = new ObjectMapper();
        String json = jacksonMapper.writeValueAsString(folder);
        assertNotNull(json);
    }
}
