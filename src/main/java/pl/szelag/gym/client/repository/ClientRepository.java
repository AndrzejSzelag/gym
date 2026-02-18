package pl.szelag.gym.client.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.szelag.gym.client.dto.ClientDtoSummary;
import pl.szelag.gym.client.entity.Client;

import java.util.Optional;

/** Repository for Client entities with eager address fetching and projections. */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /** client with eagerly fetched address by ID */
    @EntityGraph(attributePaths = "address")
    Optional<Client> findById(Long id);

    /** client with eagerly fetched address by email */
    @EntityGraph(attributePaths = "address")
    Optional<Client> findByEmail(String email);

    /** client with specific email excluding given ID */
    Optional<Client> findByEmailAndIdNot(String email, Long id);

    /**  paged summary projection of all clients */
    @EntityGraph(attributePaths = "address")
    @Query("SELECT c FROM Client c")
    Page<ClientDtoSummary> findAllSummaries(Pageable pageable);

    /** @param keyword search keyword @param pageable pagination settings @return paged filtered summaries */
    @EntityGraph(attributePaths = "address")
    @Query("SELECT c FROM Client c WHERE " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :kw, '%'))")
    Page<ClientDtoSummary> searchSummaries(@Param("kw") String keyword, Pageable pageable);

    /** @param keyword search keyword @return count of clients matching keyword */
    @Query("SELECT COUNT(c) FROM Client c WHERE " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :kw, '%'))")
    long countByKeyword(@Param("kw") String keyword);
}