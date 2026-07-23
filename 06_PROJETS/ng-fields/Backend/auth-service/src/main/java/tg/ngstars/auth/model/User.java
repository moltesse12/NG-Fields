package tg.ngstars.auth.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "keycloak_id"),
            @UniqueConstraint(columnNames = "username"),
            @UniqueConstraint(columnNames = "email")
       })
public class User {

    @Id
    private UUID id;

    @Column(name = "keycloak_id", nullable = false)
    private UUID keycloakId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String role;

    private String phone;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "company_id")
    private UUID companyId;

    @Column(name = "must_change_password", nullable = false)
    private Boolean mustChangePassword = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        var now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() { updatedAt = OffsetDateTime.now(); }

    public User() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getKeycloakId() { return keycloakId; }
    public void setKeycloakId(UUID keycloakId) { this.keycloakId = keycloakId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public Boolean getMustChangePassword() { return mustChangePassword; }
    public void setMustChangePassword(Boolean mustChangePassword) { this.mustChangePassword = mustChangePassword; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public UUID getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(UUID updatedBy) { this.updatedBy = updatedBy; }
    public Long getVersion() { return version; }
}
