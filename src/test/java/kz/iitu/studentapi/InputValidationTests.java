package kz.iitu.studentapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class InputValidationTests {
    private static final String BASE = StudentController.BASE_PATH;
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private Map<String, Object> student() {
        return new java.util.HashMap<>(Map.of("firstName", "Eldar", "lastName", "Sarsembayev",
            "email", "37006@iitu.edu.kz", "groupName", "IT2-2301"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "12.5", "true"})
    void rejectsNonStringNames(String scalar) throws Exception {
        String body = mapper.writeValueAsString(student()).replace("\"Eldar\"", scalar);
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest());
        mvc.perform(get(BASE)).andExpect(content().json("[]"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"firstName", "lastName", "groupName"})
    void rejectsUnicodeWhitespaceAfterNormalization(String field) throws Exception {
        Map<String, Object> body = student();
        body.put(field, "\u2003");
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors." + field).exists());
        mvc.perform(get(BASE)).andExpect(content().json("[]"));
    }

    @Test void normalizesAllFieldsBeforeValidation() throws Exception {
        Map<String, Object> body = Map.of("firstName", "\u2003Eldar\u2003", "lastName", " Sarsembayev ",
            "email", " 37006@IITU.EDU.KZ ", "groupName", " IT2-2301 ");
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.firstName").value("Eldar"))
            .andExpect(jsonPath("$.lastName").value("Sarsembayev"))
            .andExpect(jsonPath("$.email").value("37006@iitu.edu.kz"))
            .andExpect(jsonPath("$.groupName").value("IT2-2301"));
    }

    @Test void rejectsNonBreakingWhitespaceName() throws Exception {
        Map<String, Object> body = student();
        body.put("firstName", "\u00a0\u202f");
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.firstName").exists());
    }

    @Test void duplicatePaddedEmailReturns409() throws Exception {
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(student())))
            .andExpect(status().isCreated());
        Map<String, Object> duplicate = student();
        duplicate.put("email", " 37006@IITU.EDU.KZ ");
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(duplicate)))
            .andExpect(status().isConflict());
    }

    @Test void rejectsEmailWhoseLowercaseFormIsTooLong() throws Exception {
        Map<String, Object> body = student();
        body.put("email", "\u0130".repeat(64) + "@" + String.join(".", java.util.Collections.nCopies(3, "a".repeat(60))));
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.email").exists());
    }

    @Test void invalidUpdateDoesNotEraseTheExistingName() throws Exception {
        String created = mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(student())))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(created).get("id").asLong();
        Map<String, Object> invalid = student();
        invalid.put("firstName", "\u2003");
        mvc.perform(put(BASE + "/" + id).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest());
        mvc.perform(get(BASE + "/" + id)).andExpect(jsonPath("$.firstName").value("Eldar"));
    }

    @Test void acceptsInternationalNames() throws Exception {
        Map<String, Object> body = student();
        body.put("firstName", "Эльдар");
        body.put("lastName", "Сарсембаев");
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.firstName").value("Эльдар"));
    }
}
