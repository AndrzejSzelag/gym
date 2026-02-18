package pl.szelag.gym.client.entity;

import pl.szelag.gym.common.api.AddressProvider;

import java.time.LocalDate;

/** * Command encapsulating intent to update Client aggregate.
 * @param firstName client's first name
 * @param lastName client's last name
 * @param email contact email
 * @param phone contact number
 * @param expirationDate membership end date
 * @param address physical address data
 */
public record ClientUpdateCommand(
        String firstName,
        String lastName,
        String email,
        String phone,
        LocalDate expirationDate,
        AddressProvider address
) {}