package tg.ngstars.interv.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record UpdateInterventionRequest(
    @Schema(description = "Latitude du lieu d'intervention", example = "6.1319")
    @DecimalMin(value = "-90.0", message = "La latitude doit être comprise entre -90.0 et 90.0")
    @DecimalMax(value = "90.0", message = "La latitude doit être comprise entre -90.0 et 90.0")
    Double gpsLatitude,

    @Schema(description = "Longitude du lieu d'intervention", example = "1.2228")
    @DecimalMin(value = "-180.0", message = "La longitude doit être comprise entre -180.0 et 180.0")
    @DecimalMax(value = "180.0", message = "La longitude doit être comprise entre -180.0 et 180.0")
    Double gpsLongitude
) {}
