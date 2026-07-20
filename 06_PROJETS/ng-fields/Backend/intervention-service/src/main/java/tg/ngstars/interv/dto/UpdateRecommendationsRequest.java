package tg.ngstars.interv.dto;

import jakarta.validation.constraints.Size;

public record UpdateRecommendationsRequest(
    @Size(max = 5000) String recommendations
) {}
