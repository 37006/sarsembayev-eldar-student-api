package kz.iitu.studentapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ControlledPipelineTests {
    @Autowired MockMvc mvc;
    @Test void invalidEmailIsRejected() throws Exception {
        // Deliberately wrong expectation for the isolated troubleshooting demonstration.
        mvc.perform(post(StudentController.BASE_PATH).contentType(MediaType.APPLICATION_JSON)
            .content("{\"firstName\":\"Eldar\",\"lastName\":\"Sarsembayev\",\"email\":\"invalid\",\"groupName\":\"IT2-2301\"}"))
            .andExpect(status().isCreated());
    }
}
