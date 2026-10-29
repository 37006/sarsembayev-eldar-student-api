package kz.iitu.studentapi;

import jakarta.validation.constraints.*;
import java.util.Locale;
import java.util.regex.Pattern;

public record StudentRequest(
    @NotBlank @Size(max = 80) String firstName,
    @NotBlank @Size(max = 80) String lastName,
    @NotBlank @Email @Size(max = 254) String email,
    @NotBlank @Size(max = 30) String groupName
) {
    private static final Pattern OUTER_SPACE = Pattern.compile("^\\s+|\\s+$", Pattern.UNICODE_CHARACTER_CLASS);

    public StudentRequest {
        firstName = normalize(firstName);
        lastName = normalize(lastName);
        email = normalize(email);
        if (email != null) email = email.toLowerCase(Locale.ROOT);
        groupName = normalize(groupName);
    }

    private static String normalize(String value) {
        return value == null ? null : OUTER_SPACE.matcher(value).replaceAll("");
    }
}
