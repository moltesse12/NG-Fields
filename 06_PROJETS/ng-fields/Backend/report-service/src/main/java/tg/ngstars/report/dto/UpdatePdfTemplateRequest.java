package tg.ngstars.report.dto;

public record UpdatePdfTemplateRequest(
    String name,
    String description,
    String config,
    Boolean isDefault
) {}
