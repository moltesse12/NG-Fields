package tg.ngstars.auth.model;

import java.net.InetAddress;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_access_log")
public class CompanyAccessLog {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private CompanyUser user;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(length = 100)
    private String resource;

    @Column(name = "resource_id")
    private UUID resourceId;

    @Column(name = "ip_address")
    private InetAddress ipAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        createdAt = OffsetDateTime.now();
    }

    public CompanyAccessLog() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public CompanyUser getUser() { return user; }
    public void setUser(CompanyUser user) { this.user = user; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public UUID getResourceId() { return resourceId; }
    public void setResourceId(UUID resourceId) { this.resourceId = resourceId; }
    public InetAddress getIpAddress() { return ipAddress; }
    public void setIpAddress(InetAddress ipAddress) { this.ipAddress = ipAddress; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
