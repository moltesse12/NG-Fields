package tg.ngstars.interv.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "interventions",
       indexes = {
           @Index(name = "idx_interventions_client_id", columnList = "client_id"),
           @Index(name = "idx_interventions_status", columnList = "status"),
           @Index(name = "idx_interventions_assigned_to", columnList = "assigned_to"),
           @Index(name = "idx_interventions_created_at", columnList = "created_at")
       })
@Getter @Setter @ToString(exclude = "items")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Intervention {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "client_phone")
    private String clientPhone;

    @Column(name = "client_address")
    private String clientAddress;

    @Column(name = "equipment_type")
    private String equipmentType;

    @Column(name = "equipment_brand")
    private String equipmentBrand;

    @Column(name = "equipment_model")
    private String equipmentModel;

    @Column(name = "equipment_serial")
    private String equipmentSerial;

    @Column(name = "equipment_location")
    private String equipmentLocation;

    @Column(name = "reported_issue")
    private String reportedIssue;

    private String diagnosis;

    @Column(name = "work_done")
    private String workDone;

    @Column(nullable = false)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "intervention_date")
    private OffsetDateTime interventionDate;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "assigned_to")
    private UUID assignedTo;

    @Column(name = "site_address")
    private String siteAddress;

    @Column(name = "site_city")
    private String siteCity;

    @Column(name = "estimated_cost")
    private BigDecimal estimatedCost;

    @Column(name = "gps_latitude")
    @DecimalMin(value = "-90.0", message = "La latitude doit être comprise entre -90.0 et 90.0")
    @DecimalMax(value = "90.0", message = "La latitude doit être comprise entre -90.0 et 90.0")
    private Double gpsLatitude;

    @Column(name = "gps_longitude")
    @DecimalMin(value = "-180.0", message = "La longitude doit être comprise entre -180.0 et 180.0")
    @DecimalMax(value = "180.0", message = "La longitude doit être comprise entre -180.0 et 180.0")
    private Double gpsLongitude;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Column(name = "client_signature")
    private String clientSignature;

    @Column(name = "technician_signature")
    private String technicianSignature;

    @Column(name = "manager_signature")
    private String managerSignature;

    @Column(name = "signed_at")
    private OffsetDateTime signedAt;

    @Column(name = "departure_time")
    private OffsetDateTime departureTime;

    @Column(name = "arrival_time")
    private OffsetDateTime arrivalTime;

    @Column(name = "start_time")
    private OffsetDateTime startTime;

    @Column(name = "end_time")
    private OffsetDateTime endTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(length = 20)
    private String result;

    @Column(name = "follow_up_recommended")
    @Builder.Default
    private Boolean followUpRecommended = false;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "local_id", unique = true)
    private String localId;

    private String notes;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Version
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "intervention", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<InterventionItem> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        var now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
