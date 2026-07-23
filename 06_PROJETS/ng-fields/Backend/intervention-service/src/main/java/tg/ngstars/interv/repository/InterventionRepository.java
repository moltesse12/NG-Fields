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

    @EntityGraph(attributePaths = {"items"})
    Page<Intervention> findByActiveTrueAndClientIdAndStatusOrderByCreatedAtDesc(UUID clientId, String status, Pageable pageable);

    @Modifying
    @Query("UPDATE Intervention i SET i.clientName = :name, i.clientEmail = :email, i.clientPhone = :phone, i.clientAddress = :address WHERE i.clientId = :clientId AND i.active = true")
    int syncClientData(@Param("clientId") UUID clientId,
                       @Param("name") String name,
                       @Param("email") String email,
                       @Param("phone") String phone,
                       @Param("address") String address);

    @Query("SELECT i.status, COUNT(i) FROM Intervention i WHERE i.active = true GROUP BY i.status")
    List<Object[]> countByStatus();

    @Query("SELECT COUNT(i) FROM Intervention i WHERE i.active = true")
    long countActive();

    @Query("SELECT COUNT(i) FROM Intervention i")
    long countAll();

    @Query("SELECT COUNT(i) FROM Intervention i WHERE i.active = true AND i.status = 'COMPLETED'")
    long countCompleted();

    @Query("SELECT COUNT(i) FROM Intervention i WHERE i.active = true AND i.status = 'PENDING'")
    long countPending();

    @Query("SELECT COUNT(i) FROM Intervention i WHERE i.active = true AND i.status = 'CANCELLED'")
    long countCancelled();

    @Query("SELECT COUNT(i) FROM Intervention i WHERE i.active = true AND i.assignedTo IS NOT NULL")
    long countAssigned();

    @Query("SELECT AVG(CAST(i.durationMinutes AS double)) FROM Intervention i WHERE i.active = true AND i.durationMinutes IS NOT NULL")
    Double averageDurationMinutes();

    @Query("SELECT COALESCE(SUM(i.estimatedCost), 0) FROM Intervention i WHERE i.active = true")
    java.math.BigDecimal sumEstimatedCost();

    @EntityGraph(attributePaths = {"items"})
    List<Intervention> findByActiveTrueAndAssignedToAndInterventionDateBetweenOrderByInterventionDateAsc(
            UUID assignedTo,
            java.time.OffsetDateTime startDate,
            java.time.OffsetDateTime endDate);

    @Query("SELECT COUNT(DISTINCT i.clientId) FROM Intervention i WHERE i.active = true")
    long countDistinctClients();

    @Query("SELECT COUNT(DISTINCT i.assignedTo) FROM Intervention i WHERE i.active = true AND i.assignedTo IS NOT NULL")
    long countDistinctTechnicians();

    @Query("SELECT COALESCE(SUM(i.totalCost), 0) FROM Intervention i WHERE i.active = true AND i.totalCost IS NOT NULL")
    java.math.BigDecimal sumTotalCost();

    @EntityGraph(attributePaths = {"items"})
    List<Intervention> findFirst100ByActiveTrueOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"items"})
    List<Intervention> findFirst100ByActiveTrueAndStatusOrderByCreatedAtDesc(String status);

    @EntityGraph(attributePaths = {"items"})
    List<Intervention> findFirst100ByActiveTrueAndAssignedToOrderByCreatedAtDesc(UUID assignedTo);

    @EntityGraph(attributePaths = {"items"})
    List<Intervention> findFirst100ByActiveTrueAndAssignedToAndStatusOrderByCreatedAtDesc(UUID assignedTo, String status);

    @Query("SELECT i.status, COUNT(i) FROM Intervention i WHERE i.active = true AND i.clientId = :clientId GROUP BY i.status")
    List<Object[]> countByClientId(@Param("clientId") UUID clientId);

    @Query("SELECT COUNT(i) FROM Intervention i WHERE i.active = true AND i.clientId = :clientId")
    long countByClientIdTotal(@Param("clientId") UUID clientId);

    @Query("SELECT COUNT(i) FROM Intervention i WHERE i.active = true AND i.clientId = :clientId AND i.assignedTo IS NOT NULL")
    long countAssignedByClientId(@Param("clientId") UUID clientId);

    @Query("SELECT AVG(CAST(i.durationMinutes AS double)) FROM Intervention i WHERE i.active = true AND i.clientId = :clientId AND i.durationMinutes IS NOT NULL")
    Double averageDurationByClientId(@Param("clientId") UUID clientId);

    @Query("SELECT COALESCE(SUM(i.estimatedCost), 0) FROM Intervention i WHERE i.active = true AND i.clientId = :clientId")
    java.math.BigDecimal sumEstimatedCostByClientId(@Param("clientId") UUID clientId);

    @Query("SELECT COALESCE(SUM(i.totalCost), 0) FROM Intervention i WHERE i.active = true AND i.clientId = :clientId AND i.totalCost IS NOT NULL")
    java.math.BigDecimal sumTotalCostByClientId(@Param("clientId") UUID clientId);

    @Query("SELECT COUNT(DISTINCT i.assignedTo) FROM Intervention i WHERE i.active = true AND i.clientId = :clientId AND i.assignedTo IS NOT NULL")
    long countDistinctTechniciansByClientId(@Param("clientId") UUID clientId);
}
