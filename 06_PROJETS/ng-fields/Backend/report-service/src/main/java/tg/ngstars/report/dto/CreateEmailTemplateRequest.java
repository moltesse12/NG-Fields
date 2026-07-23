package tg.ngstars.report.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateEmailTemplateRequest(
    @NotBlank(message = "Le nom est obligatoire")
    String name,
    String description,
    @NotBlank(message = "La cle est obligatoire")
    String templateKey,
    @NotBlank(message = "Le sujet est obligatoire")
    String subject,
    @NotBlank(message = "Le corps HTML est obligatoire")
    String bodyHtml
) {}
