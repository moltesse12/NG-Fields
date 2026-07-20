package tg.ngstars.notification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;

import tg.ngstars.notification.dto.EmailRequest;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock JavaMailSender mailSender;
    @Mock SpringTemplateEngine templateEngine;
    @Mock MimeMessage mimeMessage;

    EmailService service;

    @BeforeEach
    void setUp() {
        service = new EmailService(mailSender, templateEngine);
        lenient().when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        lenient().when(templateEngine.process(anyString(), any())).thenReturn("<html><body>test</body></html>");
    }

    @Test
    void send_validTemplate_shouldSend() {
        var request = new EmailRequest("test@example.com", "Test", "intervention-notification",
                "INT-001", "ACME", "Clim", "PENDING", "Technicien");

        assertDoesNotThrow(() -> service.send(request));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void send_invalidTemplate_shouldThrow() {
        var request = new EmailRequest("test@example.com", "Test", "unknown-template",
                null, null, null, null, null);

        assertThrows(IllegalArgumentException.class, () -> service.send(request));
    }

    @Test
    void send_passwordReset_shouldSend() {
        var request = new EmailRequest("test@example.com", "Reset", "password-reset",
                null, "John", null, null, null);

        assertDoesNotThrow(() -> service.send(request));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void send_welcome_shouldSend() {
        var request = new EmailRequest("test@example.com", "Welcome", "welcome",
                "CLT-001", "ACME", null, null, null);

        assertDoesNotThrow(() -> service.send(request));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void send_interventionAssigned_shouldSend() {
        var request = new EmailRequest("tech@example.com", "Assigned", "intervention-assigned",
                "INT-001", "ACME", "Clim", null, "Tech1");

        assertDoesNotThrow(() -> service.send(request));
    }

    @Test
    void send_interventionCompleted_shouldSend() {
        var request = new EmailRequest("client@example.com", "Completed", "intervention-completed",
                "INT-001", "ACME", null, "COMPLETED", "Tech1");

        assertDoesNotThrow(() -> service.send(request));
    }

    @Test
    void send_failsAfterRetries_shouldThrow() {
        doThrow(new MailSendException("SMTP error"))
                .when(mailSender).send(any(MimeMessage.class));

        var request = new EmailRequest("test@example.com", "Test", "intervention-notification",
                null, null, null, null, null);

        assertThrows(RuntimeException.class, () -> service.send(request));
        verify(mailSender, times(3)).send(any(MimeMessage.class));
    }

    @Test
    void send_retriesThenSucceeds() {
        var request = new EmailRequest("test@example.com", "Test", "intervention-notification",
                null, null, null, null, null);

        doThrow(new MailSendException("SMTP error"))
                .doNothing()
                .when(mailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() -> service.send(request));
        verify(mailSender, times(2)).send(any(MimeMessage.class));
    }

    @Test
    void send_nullTemplateVariables_shouldNotThrow() {
        var request = new EmailRequest("test@example.com", "Test", "intervention-notification",
                null, null, null, null, null);

        assertDoesNotThrow(() -> service.send(request));
    }
}
