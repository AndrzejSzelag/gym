package pl.szelag.gym.user.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.szelag.gym.user.entity.User;

import java.util.Optional;

/** Repository for User identity management and authentication. */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** @param email user email user with eagerly fetched role */
    @EntityGraph(attributePaths = {"role"})
    Optional<User> findByEmail(String email);

    /** @param email email to check true if user exists */
    boolean existsByEmail(String email);
}