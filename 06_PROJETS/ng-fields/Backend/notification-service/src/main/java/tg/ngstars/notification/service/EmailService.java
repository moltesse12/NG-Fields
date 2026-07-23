package tg.ngstars.notification.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import io.github.resilience4j.retry.annotation.Retry;
import tg.ngstars.notification.dto.EmailRequest;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private static final Set<String> ALLOWED_TEMPLATES = Set.of(
        "intervention-notification", "password-reset", "welcome",
        "intervention-assigned", "intervention-completed");

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final DeadLetterQueueService dlqService;
    private final EmailAuditLogger auditLogger;

    public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine,
                        DeadLetterQueueService dlqService, EmailAuditLogger auditLogger) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.dlqService = dlqService;
        this.auditLogger = auditLogger;
    }

    @Retry(name = "emailService", fallbackMethod = "sendFallback")
    public void send(EmailRequest request) {
        var template = request.template();
        if (!ALLOWED_TEMPLATES.contains(template))
            throw new IllegalArgumentException("Template non autorise: " + template);

        var vars = new HashMap<String, Object>();
        vars.put("interventionRef", safeString(request.interventionRef()));
        vars.put("clientName", safeString(request.clientName()));
        vars.put("equipmentType", safeString(request.equipmentType()));
        vars.put("status", safeString(request.status()));
        vars.put("assignedTo", safeString(request.assignedTo()));
        var ctx = new Context();
        ctx.setVariables(vars);
        var html = templateEngine.process("email/" + template, ctx);

        var mime = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(mime, true, "UTF-8");
        helper.setTo(request.to());
        helper.setSubject(request.subject());
        helper.setText(html, true);
        mailSender.send(mime);

        log.info("Email sent to {} subject='{}'", request.to(), request.subject());
        auditLogger.logSent(request.to(), request.subject(), template, "SENT");
    }

    private void sendFallback(EmailRequest request, Throwable t) {
        log.error("Échec envoi email à {} après retries: {}", request.to(), t.getMessage());
        auditLogger.logFailed(request.to(), request.subject(), request.template(), t.getMessage());
        dlqService.enqueue(
                "EMAIL",
                request.to(),
                request.subject(),
                request.template(),
                request.toString(),
                t.getMessage());
    }

    private static String safeString(String value) {
        return value != null ? value : "";
    }
}
