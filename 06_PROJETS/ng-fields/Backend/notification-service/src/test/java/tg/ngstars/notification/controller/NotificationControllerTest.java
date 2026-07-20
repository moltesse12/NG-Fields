package tg.ngstars.notification.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import tg.ngstars.notification.dto.EmailRequest;
import tg.ngstars.notification.service.EmailService;
import tg.ngstars.notification.config.SecurityConfig;

@WebMvcTest(NotificationController.class)
@Import(SecurityConfig.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private EmailService emailService;

    @Test
    void sendEmail_shouldReturn202() throws Exception {
        var request = new EmailRequest(
                "user@test.com", "Test Subject", "intervention-notification",
                "INT-001", "John Doe", "Printer", "COMPLETED", "Tech1");

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(authenticated()))
                .andExpect(status().isAccepted());

        verify(emailService).send(any(EmailRequest.class));
    }

    @Test
    void sendEmail_invalidBody_shouldReturn400() throws Exception {
        var request = new EmailRequest(
                "", "", "", null, null, null, null, null);

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(authenticated()))
                .andExpect(status().isBadRequest());
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor authenticated() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                .jwt(jwt -> jwt.claim("sub", UUID.randomUUID().toString()));
    }
}
