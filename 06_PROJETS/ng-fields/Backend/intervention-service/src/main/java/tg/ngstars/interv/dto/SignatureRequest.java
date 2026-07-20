package tg.ngstars.interv.dto;

import jakarta.validation.constraints.NotBlank;

public record SignatureRequest(
    @NotBlank String imageBase64
) {}
