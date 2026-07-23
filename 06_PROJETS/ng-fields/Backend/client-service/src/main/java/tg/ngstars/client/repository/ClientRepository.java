package tg.ngstars.client.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tg.ngstars.client.model.Client;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    boolean existsByEmail(String email);

    boolean existsByCompanyNameIgnoreCase(String companyName);

    Optional<Client> findByReference(String reference);

    @EntityGraph(attributePaths = {"contacts"})
    Page<Client> findByActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"contacts"})
    @Query("""
        SELECT c FROM Client c
        WHERE c.active = true
        AND (
            LOWER(c.companyName) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(c.contactName) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(c.email) LIKE LOWER(CONCAT('%', :q, '%'))
            OR c.phone LIKE CONCAT('%', :q, '%')
        )
        """)
    Page<Client> search(@Param("q") String query, Pageable pageable);

    @Query(value = "SELECT LPAD(CAST(nextval('client_ref_seq') AS TEXT), 4, '0')", nativeQuery = true)
    String nextReference();
}
