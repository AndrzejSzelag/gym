package pl.szelag.gym.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findById(final Long id);

    Client findByEmail(String email);

    Page<Client> findAll(Pageable pageable);

    @Query("SELECT c FROM Client c WHERE c.lastName LIKE  %?1%")
    List<Client> search(String keyword);
}