package pl.szelag.gym.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.szelag.gym.user.entity.Role;

import java.util.Optional;

/** Repository for Role entities used during system startup and registration. */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /** @param name unique authority name optional role entity */
    @Query("SELECT r FROM Role r WHERE r.name = :name")
    Optional<Role> findByName(String name);
}