package net.omb.photogallery;

/**
 * Created by volodymyr.bodnar on 9/7/2017.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by volodymyr.bodnar on 9/6/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RestTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private static final ObjectMapper jacksonMapper = new ObjectMapper();

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).defaultRequest(post("/*").accept(MediaType.APPLICATION_JSON)).dispatchOptions(true).build();
    }

    @Test
    public void restAuthorizationTest() throws Exception {
        mockMvc.perform(
                post("/testService")
                        .param("throwException", "false")
        ).andExpect(status().isForbidden())
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

//        mockMvc.perform(
//                post("/InstanceServices")
//                        .param("throwException", "false")
//        ).andExpect(status().isForbidden());
    }

    @Test
    public void mapTest() throws Exception {
//        net.omb.photogallery.model.json.Test test = new net.omb.photogallery.model.json.Test();
//        mockMvc.perform(
//                post("/api/uploadPhoto")
//                        .param("test", jacksonMapper.writeValueAsString(test))
//        ).andExpect(status().isOk())
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));

//        mockMvc.perform(
//                post("/InstanceServices")
//                        .param("throwException", "false")
//        ).andExpect(status().isForbidden());
    }

}
