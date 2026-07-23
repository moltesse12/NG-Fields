package tg.ngstars.interv.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public record SyncBatchRequest(
    @NotEmpty @Max(10) @Min(1) List<@Valid SyncRequest> interventions
) {}
