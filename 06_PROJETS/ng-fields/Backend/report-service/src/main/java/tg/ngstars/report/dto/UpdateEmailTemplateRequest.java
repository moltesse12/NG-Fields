package tg.ngstars.report.dto;

public record UpdateEmailTemplateRequest(
    String name,
    String description,
    String subject,
    String bodyHtml,
    Boolean isActive
) {}
