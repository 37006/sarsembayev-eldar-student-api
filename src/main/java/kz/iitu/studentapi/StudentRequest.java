package kz.iitu.studentapi;

import jakarta.validation.constraints.*;

public record StudentRequest(
    @NotBlank @Size(max = 80) String firstName,
    @NotBlank @Size(max = 80) String lastName,
    @NotBlank @Email @Size(max = 254) String email,
    @NotBlank @Size(max = 30) String groupName
) {}
