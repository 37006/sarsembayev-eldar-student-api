package kz.iitu.studentapi;

import java.sql.SQLException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import static org.junit.jupiter.api.Assertions.*;

class ApiExceptionHandlerTests {
    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test void recognizesTheEmailUniqueConstraint() {
        var cause = new ConstraintViolationException("duplicate", new SQLException("duplicate", "23505"), "uk_students_email");
        var response = handler.duplicate(new DataIntegrityViolationException("save failed", cause));
        assertEquals(409, response.getStatusCode().value());
        assertEquals("A student with this email already exists", response.getBody().getDetail());
    }

    @Test void doesNotReportAnUnrelatedDatabaseErrorAsDuplicateEmail() {
        var cause = new ConstraintViolationException("not null", new SQLException("not null", "23502"), "students_first_name");
        var response = handler.duplicate(new DataIntegrityViolationException("save failed", cause));
        assertEquals(500, response.getStatusCode().value());
        assertEquals("The student could not be saved", response.getBody().getDetail());
        assertFalse(response.getBody().getDetail().contains("email"));
    }
}
