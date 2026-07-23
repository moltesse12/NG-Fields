package tg.ngstars.report.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePdfTemplateRequest(
    @NotBlank(message = "Le nom est obligatoire")
    String name,
    String description,
    String templateType,
    @NotBlank(message = "La configuration est obligatoire")
    String config
) {}
