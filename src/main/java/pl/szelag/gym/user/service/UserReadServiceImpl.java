package pl.szelag.gym.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szelag.gym.user.dto.UserDto;
import pl.szelag.gym.user.entity.User;
import pl.szelag.gym.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/** Read-only service for fetching user data and performing DTO mapping. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
class UserReadServiceImpl implements UserReadService {

    private final UserRepository userRepository;

    /** list of all users as DTOs sorted by ID descending */
    @Override
    public List<UserDto> getUsers() {
        log.debug("Fetching all users for display purposes");
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    /** @param email unique user email optional containing user entity if found */
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /** @param user entity to map UserDto with sanitized names and nullified password */
    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(null)
                .build();
    }
}