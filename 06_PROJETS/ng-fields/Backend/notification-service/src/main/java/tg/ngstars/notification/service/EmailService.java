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

import tg.ngstars.notification.dto.EmailRequest;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    // ponytail: add new template names here when creating new .html files in templates/email/
    private static final Set<String> ALLOWED_TEMPLATES = Set.of(
        "intervention-notification", "password-reset", "welcome",
        "intervention-assigned", "intervention-completed");

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    // ponytail: simple retry loop, switch to @Retryable + spring-retry if needed
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

        Exception lastException = null;
        var retries = 3;
        for (int attempt = 1; attempt <= retries; attempt++) {
            try {
                var mime = mailSender.createMimeMessage();
                var helper = new MimeMessageHelper(mime, true, "UTF-8");
                helper.setTo(request.to());
                helper.setSubject(request.subject());
                helper.setText(html, true);
                mailSender.send(mime);
                log.info("Email sent to {} subject='{}'", request.to(), request.subject());
                return;
            } catch (Exception e) {
                lastException = e;
                if (attempt < retries) {
                    log.warn("Failed to send email (attempt {}/{}): {}", attempt, retries, e.getMessage());
                    try { Thread.sleep(1000L * attempt); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                }
            }
        }
        log.error("Failed to send email to {} after {} attempts", request.to(), retries);
        throw new RuntimeException("Failed to send email after " + retries + " attempts", lastException);
    }

    private static String safeString(String value) {
        return value != null ? value : "";
    }
}
