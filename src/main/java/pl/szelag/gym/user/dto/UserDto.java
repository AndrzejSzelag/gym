package pl.szelag.gym.user.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public final class UserDto {

    @NotNull(message = "{constraint.notnull}")
    @Column(name = "FIRST_NAME", length = 32, nullable = false)
    @Size(min = 3, max = 32, message = "{constraint.string.length.notinrange}")
    @Pattern(regexp = "^[\\p{L}\\'-]*$", message = "{constraint.string.incorrectchar}")
    private String firstName;

    @NotNull(message = "{constraint.notnull}")
    @Column(name = "LAST_NAME", length = 32, nullable = false)
    @Size(min = 3, max = 32, message = "{constraint.string.length.notinrange}")
    @Pattern(regexp = "^[\\p{L}\\'-]*$", message = "{constraint.string.incorrectchar}")
    private String lastName;

    @NotNull(message = "{constraint.notnull}")
    @Column(name = "EMAIL", length = 32, nullable = false, unique = true)
    @Size(min = 3, max = 32, message = "{constraint.string.length.notinrange}")
    @Email
    private String email;

    @NotNull(message = "{constraint.notnull}")
    @Column(name = "PASSWORD", length = 64, nullable = false)
    @Size(min = 1, max = 64, message = "{constraint.string.length.notinrange}") // 60 signs for bcrypt
    private String password;
}