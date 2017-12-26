package net.omb.photogallery;

/**
 * Created by volodymyr.bodnar on 9/7/2017.
 */

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).dispatchOptions(true).build();
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


}
