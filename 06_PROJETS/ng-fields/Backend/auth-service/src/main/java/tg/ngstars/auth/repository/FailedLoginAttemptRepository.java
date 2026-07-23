package tg.ngstars.auth.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tg.ngstars.auth.model.FailedLoginAttempt;

@Repository
public interface FailedLoginAttemptRepository extends JpaRepository<FailedLoginAttempt, UUID> {

    long countByUsernameAndSuccessfulFalseAndAttemptedAtAfter(String username, OffsetDateTime since);

    long countByIpAddressAndSuccessfulFalseAndAttemptedAtAfter(String ipAddress, OffsetDateTime since);

    List<FailedLoginAttempt> findTop20ByUsernameOrderByAttemptedAtDesc(String username);

    @Modifying
    @Query("DELETE FROM FailedLoginAttempt f WHERE f.attemptedAt < :cutoff")
    void deleteOlderThan(OffsetDateTime cutoff);
}
