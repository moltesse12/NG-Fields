package tg.ngstars.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequest(
    @NotBlank @Email String to,
    @NotBlank String subject,
    @NotBlank String template,
    String interventionRef,
    String clientName,
    String equipmentType,
    String status,
    String assignedTo
) {}
