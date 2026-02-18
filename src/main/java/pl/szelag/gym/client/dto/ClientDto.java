package pl.szelag.gym.client.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import pl.szelag.gym.client.entity.ClientUpdateCommand;

import java.time.LocalDate;

/** DTO for client web-binding and validation. Implements {@link ClientLogic} for UI consistency. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ClientDto implements ClientLogic {

    /** client unique identifier */
    private Long id;

    /** client's first name */
    @NotBlank(message = "{validation.firstName.notBlank}")
    @Size(min = 2, max = 50, message = "{validation.firstName.size}")
    @Pattern(regexp = "^[\\p{L}\\s'-]+$", message = "{validation.firstName.pattern}")
    private String firstName;

    /** client's last name */
    @NotBlank(message = "{validation.lastName.notBlank}")
    @Size(min = 2, max = 50, message = "{validation.lastName.size}")
    @Pattern(regexp = "^[\\p{L}\\s'-]+$", message = "{validation.lastName.pattern}")
    private String lastName;

    /** client's unique email address */
    @NotBlank(message = "{validation.email.notBlank}")
    @Size(min = 6, max = 64, message = "{validation.email.size}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    /** client's contact phone number */
    @NotBlank(message = "{validation.phone.notBlank}")
    @Size(min = 3, max = 9, message = "{validation.phone.size}")
    @Pattern(regexp = "^[0-9+ ]+$", message = "{validation.phone.invalid}")
    private String phone;

    /** date when the client was added to the system */
    private LocalDate registrationDate;

    /** current membership expiration date */
    private LocalDate expirationDate;

    /** validated address transfer object */
    @Valid
    private AddressDto address;

    /** client DTO with empty fields and initialized address */
    public static ClientDto empty() {
        return ClientDto.builder().firstName("").lastName("").email("").phone("")
                .address(AddressDto.empty())
                .build();
    }

    /** command object for domain-level updates */
    public ClientUpdateCommand toCommand() {
        return new ClientUpdateCommand(firstName, lastName, email, phone,
                expirationDate,
                address
        );
    }
}