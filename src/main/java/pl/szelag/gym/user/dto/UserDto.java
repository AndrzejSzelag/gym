package pl.szelag.gym.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * DTO for user registration and profile management.
 * @param firstName first name
 * @param lastName last name
 * @param email unique email
 * @param password user secret
 */
@Builder
public record UserDto(
        @NotBlank(message = "{validation.firstName.notBlank}")
        @Size(min = 2, max = 50, message = "{validation.firstName.size}")
        @Pattern(regexp = "^[\\p{L}\\s\\'-]+$", message = "{validation.firstName.pattern}")
        String firstName,

        @NotBlank(message = "{validation.lastName.notBlank}")
        @Size(min = 2, max = 50, message = "{validation.lastName.size}")
        @Pattern(regexp = "^[\\p{L}\\s\\'-]+$", message = "{validation.lastName.pattern}")
        String lastName,

        @NotBlank(message = "{validation.email.notBlank}")
        @Email(message = "{validation.email.invalid}")
        @Size(min = 6, max = 64, message = "{validation.email.size}")
        String email,

        @NotBlank(message = "{validation.password.notBlank}")
        @Size(min = 6, max = 64, message = "{validation.password.size}")
        String password
) {

    /** combined first and last name */
    public String getFullName() {
        return (firstName + " " + (lastName != null ? lastName : "")).trim();
    }

    /** user DTO with empty strings */
    public static UserDto empty() {
        return new UserDto("", "", "", "");
    }
}