package br.com.ss.blog.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO for partial user updates. Fields are optional.
 */
public record UserUpdateDTO(
    String firstName,

    String lastName,

    @Email(message = "If provided, email must be valid")
    String email,

    @Past(message = "If provided, birth date must be in the past")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate birthDate
) {
}