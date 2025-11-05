package br.com.ss.blog.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.Instant;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for User creation and response.
 * Includes validation constraints for input processing.
 */
public record UserDTO(

    // For response only
    UUID id,

    @NotBlank(message = "First name cannot be empty")
    String firstName,

    @NotBlank(message = "Last name cannot be empty")
    String lastName,

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    String email,

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate birthDate,

    // For response only
    Instant createdAt

) {

}
