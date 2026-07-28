package tg.ngstars.auth.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "failed_login_attempts",
       indexes = {
           @Index(name = "idx_failed_login_username", columnList = "username"),
           @Index(name = "idx_failed_login_ip", columnList = "ip_address"),
           @Index(name = "idx_failed_login_username_attempt", columnList = "username, successful, attempted_at")
       })
@Getter @Setter
public class FailedLoginAttempt {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private boolean successful;

    @Column(name = "attempted_at", nullable = false, updatable = false)
    private OffsetDateTime attemptedAt;

    @Column(name = "locked_until")
    private OffsetDateTime lockedUntil;

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (attemptedAt == null) attemptedAt = OffsetDateTime.now();
    }

    public FailedLoginAttempt() {}

    public FailedLoginAttempt(String username, String ipAddress, boolean successful) {
        this.username = username;
        this.ipAddress = ipAddress;
        this.successful = successful;
    }
}
