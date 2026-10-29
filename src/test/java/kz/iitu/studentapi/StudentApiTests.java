package kz.iitu.studentapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class StudentApiTests {
    static final String BASE = StudentController.BASE_PATH;
    static final String VALID = """
        {"firstName":"Eldar","lastName":"Sarsembayev","email":"37006@iitu.edu.kz","groupName":"IT2-2301"}
        """;
    @Autowired MockMvc mvc;
    @Test void displaysStudentIdentity() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(jsonPath("$.studentId").value("37006"));
    }
    @Test void healthEndpointWorks() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
    }
    @Test void createsStudentAndLocation() throws Exception {
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(VALID))
            .andExpect(status().isCreated()).andExpect(header().string("Location", containsString(BASE + "/")))
            .andExpect(jsonPath("$.lastName").value("Sarsembayev"));
    }
    @Test void emptyListIsValid() throws Exception {
        mvc.perform(get(BASE)).andExpect(status().isOk()).andExpect(content().json("[]"));
    }
    @Test void unknownStudentReturns404() throws Exception {
        mvc.perform(get(BASE + "/99999999")).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
    }
    @Test void deleteMissingReturns404() throws Exception {
        mvc.perform(delete(BASE + "/99999999")).andExpect(status().isNotFound());
    }
    @Test void invalidEmailReturns400() throws Exception {
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(VALID.replace("37006@iitu.edu.kz", "invalid")))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.email").exists());
    }
    @Test void blankNameReturns400() throws Exception {
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(VALID.replace("Eldar", "   ")))
            .andExpect(status().isBadRequest());
    }
    @Test void missingFieldsReturn400() throws Exception {
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.firstName").exists());
    }
    @Test void malformedJsonReturns400() throws Exception {
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("{"))
            .andExpect(status().isBadRequest());
    }
    @Test void invalidIdReturns400() throws Exception {
        mvc.perform(get(BASE + "/abc")).andExpect(status().isBadRequest());
    }
    @Test void oversizedNameReturns400() throws Exception {
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(VALID.replace("Eldar", "A".repeat(81))))
            .andExpect(status().isBadRequest());
    }

    @Autowired com.fasterxml.jackson.databind.ObjectMapper mapper;
    private long createStudent() throws Exception {
        String json = mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(VALID))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(json).get("id").asLong();
    }
    @Test void updatesExistingStudent() throws Exception {
        long id = createStudent();
        mvc.perform(put(BASE + "/" + id).contentType(MediaType.APPLICATION_JSON).content(VALID.replace("IT2-2301", "IT2-2301-UPDATED")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.groupName").value("IT2-2301-UPDATED"));
        mvc.perform(get(BASE + "/" + id)).andExpect(jsonPath("$.groupName").value("IT2-2301-UPDATED"));
    }
    @Test void updateMissingReturns404() throws Exception {
        mvc.perform(put(BASE + "/99999999").contentType(MediaType.APPLICATION_JSON).content(VALID))
            .andExpect(status().isNotFound());
    }
    @Test void invalidUpdateReturns400() throws Exception {
        long id = createStudent();
        mvc.perform(put(BASE + "/" + id).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
        mvc.perform(get(BASE + "/" + id)).andExpect(jsonPath("$.firstName").value("Eldar"));
    }
    @Test void deletesExistingStudent() throws Exception {
        long id = createStudent();
        mvc.perform(delete(BASE + "/" + id)).andExpect(status().isNoContent());
        mvc.perform(get(BASE + "/" + id)).andExpect(status().isNotFound());
    }
    @Test void normalizesEmailCase() throws Exception {
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(VALID.replace("37006@iitu.edu.kz", "37006@IITU.EDU.KZ")))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.email").value("37006@iitu.edu.kz"));
    }
}
