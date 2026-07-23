package tg.ngstars.notification.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.junit.jupiter.api.Assertions.*;

class TemplateRenderingTest {

    private final SpringTemplateEngine templateEngine;

    TemplateRenderingTest() {
        var resolver = new org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");

        this.templateEngine = new SpringTemplateEngine();
        this.templateEngine.setTemplateResolver(resolver);
    }

    @Test
    void welcomeTemplate_renders() {
        var ctx = new Context();
        ctx.setVariable("clientName", "Jean Dupont");
        ctx.setVariable("interventionRef", "NG-0001");
        var html = templateEngine.process("email/welcome", ctx);

        assertNotNull(html);
        assertTrue(html.contains("Jean Dupont"));
        assertTrue(html.contains("NG-0001"));
        assertTrue(html.contains("Bienvenue"));
    }

    @Test
    void welcomeTemplate_fallbackOnNull() {
        var ctx = new Context();
        ctx.setVariable("clientName", null);
        ctx.setVariable("interventionRef", null);
        var html = templateEngine.process("email/welcome", ctx);

        assertNotNull(html);
        assertTrue(html.contains("Utilisateur"));
    }

    @Test
    void interventionNotificationTemplate_renders() {
        var ctx = new Context();
        ctx.setVariable("interventionRef", "NG-0042");
        ctx.setVariable("clientName", "Societe Test");
        ctx.setVariable("equipmentType", "Climatiseur");
        ctx.setVariable("status", "ASSIGNED");
        var html = templateEngine.process("email/intervention-notification", ctx);

        assertNotNull(html);
        assertTrue(html.contains("NG-0042"));
        assertTrue(html.contains("Societe Test"));
    }

    @Test
    void interventionAssignedTemplate_renders() {
        var ctx = new Context();
        ctx.setVariable("interventionRef", "NG-0099");
        ctx.setVariable("clientName", "Client ABC");
        ctx.setVariable("assignedTo", "Technicien XY");
        var html = templateEngine.process("email/intervention-assigned", ctx);

        assertNotNull(html);
        assertTrue(html.contains("NG-0099"));
    }

    @Test
    void interventionCompletedTemplate_renders() {
        var ctx = new Context();
        ctx.setVariable("interventionRef", "NG-0077");
        ctx.setVariable("clientName", "Client Final");
        ctx.setVariable("status", "COMPLETED");
        var html = templateEngine.process("email/intervention-completed", ctx);

        assertNotNull(html);
        assertTrue(html.contains("NG-0077"));
    }

    @Test
    void passwordResetTemplate_renders() {
        var ctx = new Context();
        ctx.setVariable("clientName", "Utilisateur Reset");
        var html = templateEngine.process("email/password-reset", ctx);

        assertNotNull(html);
        assertTrue(html.contains("Utilisateur Reset"));
    }

    @Test
    void allTemplates_containHtmlStructure() {
        String[] templates = {
            "email/welcome",
            "email/intervention-notification",
            "email/intervention-assigned",
            "email/intervention-completed",
            "email/password-reset"
        };

        for (String template : templates) {
            var ctx = new Context();
            ctx.setVariable("clientName", "Test");
            ctx.setVariable("interventionRef", "REF-001");
            ctx.setVariable("status", "PENDING");
            ctx.setVariable("equipmentType", "Test");
            ctx.setVariable("assignedTo", "Test");

            var html = templateEngine.process(template, ctx);
            assertNotNull(html, "Template " + template + " should render");
            assertTrue(html.contains("<!DOCTYPE html>") || html.contains("<html"),
                    "Template " + template + " should contain HTML structure");
        }
    }
}
