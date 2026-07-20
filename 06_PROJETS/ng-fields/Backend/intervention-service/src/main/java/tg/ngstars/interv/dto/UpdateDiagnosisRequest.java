package tg.ngstars.interv.dto;

import jakarta.validation.constraints.Size;

public record UpdateDiagnosisRequest(
    @Size(max = 5000) String diagnosis,
    @Size(max = 5000) String workDone
) {}
