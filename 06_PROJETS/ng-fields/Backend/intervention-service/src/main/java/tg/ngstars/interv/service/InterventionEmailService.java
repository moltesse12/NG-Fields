package tg.ngstars.interv.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import tg.ngstars.interv.config.ResendProperties;
import tg.ngstars.interv.model.Intervention;

@Service
@EnableConfigurationProperties(ResendProperties.class)
public class InterventionEmailService {

    private static final Logger log = LoggerFactory.getLogger(InterventionEmailService.class);

    private final Resend resend;
    private final ResendProperties properties;

    public InterventionEmailService(ResendProperties properties) {
        this.properties = properties;
        this.resend = new Resend(properties.apiKey());
    }

    public void sendInterventionReport(Intervention intervention, String recipientEmail) {
        var html = buildReportEmailHtml(intervention);
        var subject = "Rapport d'intervention " + intervention.getReference();

        sendWithPdfAttachment(intervention, recipientEmail, subject, html);
    }

    private void sendWithPdfAttachment(Intervention intervention, String to, String subject, String html) {
        try {
            byte[] pdfBytes = tg.ngstars.interv.service.PdfService.generate(intervention);
            String base64Pdf = java.util.Base64.getEncoder().encodeToString(pdfBytes);

            var attachment = com.resend.services.emails.model.Attachment.builder()
                    .content(base64Pdf)
                    .fileName("intervention-" + intervention.getReference() + ".pdf")
                    .contentType("application/pdf")
                    .build();

            var params = CreateEmailOptions.builder()
                    .from(properties.fromName() + " <" + properties.fromEmail() + ">")
                    .to(to)
                    .subject(subject)
                    .html(html)
                    .addAttachment(attachment)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            log.info("Email sent to {} for intervention {} (resendId={})",
                    to, intervention.getReference(), response.getId());
        } catch (ResendException e) {
            log.error("Failed to send email to {} for intervention {}: {}",
                    to, intervention.getReference(), e.getMessage(), e);
            throw new RuntimeException("Échec de l'envoi de l'email: " + e.getMessage(), e);
        }
    }

    private String buildReportEmailHtml(Intervention intervention) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                  <h2 style="color: #40946e;">Rapport d'intervention</h2>
                  <p>Bonjour,</p>
                  <p>Vous trouverez ci-joint le rapport d'intervention <strong>%s</strong>.</p>
                  <table style="width: 100%%; border-collapse: collapse; margin: 16px 0;">
                    <tr><td style="padding: 8px; border-bottom: 1px solid #eee;"><strong>Reference :</strong></td><td style="padding: 8px; border-bottom: 1px solid #eee;">%s</td></tr>
                    <tr><td style="padding: 8px; border-bottom: 1px solid #eee;"><strong>Client :</strong></td><td style="padding: 8px; border-bottom: 1px solid #eee;">%s</td></tr>
                    <tr><td style="padding: 8px; border-bottom: 1px solid #eee;"><strong>Equipement :</strong></td><td style="padding: 8px; border-bottom: 1px solid #eee;">%s %s</td></tr>
                    <tr><td style="padding: 8px; border-bottom: 1px solid #eee;"><strong>Statut :</strong></td><td style="padding: 8px; border-bottom: 1px solid #eee;">%s</td></tr>
                    <tr><td style="padding: 8px; border-bottom: 1px solid #eee;"><strong>Resultat :</strong></td><td style="padding: 8px; border-bottom: 1px solid #eee;">%s</td></tr>
                  </table>
                  <hr style="border: none; border-top: 1px solid #ddd; margin: 24px 0;" />
                  <p style="font-size: 12px; color: #888;">
                    Ceci est un email automatique. Ne repondez pas a cet email.
                  </p>
                </div>
                """.formatted(
                intervention.getReference(),
                intervention.getReference(),
                intervention.getClientName() != null ? intervention.getClientName() : "N/A",
                intervention.getEquipmentBrand() != null ? intervention.getEquipmentBrand() : "",
                intervention.getEquipmentModel() != null ? intervention.getEquipmentModel() : "",
                intervention.getStatus(),
                intervention.getResult() != null ? intervention.getResult() : "N/A"
        );
    }

    private void send(String to, String subject, String html) {
        try {
            var params = CreateEmailOptions.builder()
                    .from(properties.fromName() + " <" + properties.fromEmail() + ">")
                    .to(to)
                    .subject(subject)
                    .html(html)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            log.info("Email sent to {} (resendId={})", to, response.getId());
        } catch (ResendException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }
}
