package pl.szelag.gym.user.service;

import pl.szelag.gym.user.dto.UserDto;
import pl.szelag.gym.user.entity.User;

import java.util.List;
import java.util.Optional;

/** Read-only service for retrieving user profile data and entities. */
public interface UserReadService {

    /** list of all users as DTOs */
    List<UserDto> getUsers();

    /** @param email user email optional user entity for security processes */
    Optional<User> findByEmail(String email);
}