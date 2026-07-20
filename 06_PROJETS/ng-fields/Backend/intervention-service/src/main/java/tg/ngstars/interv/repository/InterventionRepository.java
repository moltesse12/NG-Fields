package tg.ngstars.interv.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tg.ngstars.interv.model.Intervention;

public interface InterventionRepository extends JpaRepository<Intervention, UUID> {

    Optional<Intervention> findByReference(String reference);

    @EntityGraph(attributePaths = {"items"})
    Page<Intervention> findByClientIdOrderByCreatedAtDesc(UUID clientId, Pageable pageable);

    @EntityGraph(attributePaths = {"items"})
    Page<Intervention> findByActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    boolean existsByReference(String reference);

    Optional<Intervention> findByLocalId(String localId);

    @EntityGraph(attributePaths = {"items"})
    Page<Intervention> findByActiveTrueAndStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    @EntityGraph(attributePaths = {"items"})
    Page<Intervention> findByActiveTrueAndAssignedToOrderByCreatedAtDesc(UUID assignedTo, Pageable pageable);

    @EntityGraph(attributePaths = {"items"})
    Page<Intervention> findByActiveTrueAndAssignedToAndStatusOrderByCreatedAtDesc(UUID assignedTo, String status, Pageable pageable);

    @Modifying
    @Query("UPDATE Intervention i SET i.clientName = :name, i.clientEmail = :email, i.clientPhone = :phone, i.clientAddress = :address WHERE i.clientId = :clientId AND i.active = true")
    int syncClientData(@Param("clientId") UUID clientId,
                       @Param("name") String name,
                       @Param("email") String email,
                       @Param("phone") String phone,
                       @Param("address") String address);
}
