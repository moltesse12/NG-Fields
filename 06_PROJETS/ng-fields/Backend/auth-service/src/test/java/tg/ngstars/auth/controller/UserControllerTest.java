package tg.ngstars.auth.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import tg.ngstars.auth.dto.*;
import tg.ngstars.auth.service.*;
import tg.ngstars.auth.config.RateLimitConfig;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Bucket;

@WebMvcTest(UserController.class)
@DisplayName("UserController - Tests d'integration WebMvc")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private BruteForceProtectionService bruteForceProtection;

    @MockitoBean
    private EmailVerificationService emailVerificationService;

    @MockitoBean
    private RateLimitConfig rateLimitConfig;

    @MockitoBean
    private EmailService emailService;

    private UUID userId;
    private UserResponse userResponse;
    private Jwt adminJwt;
    private Jwt userJwt;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userResponse = new UserResponse(
                userId, UUID.randomUUID(), "testuser", "test@ngstars.tg",
                "Test", "User", "TECHNICIAN", "+22890000000",
                true, null, false, true,
                OffsetDateTime.now(), OffsetDateTime.now());

        adminJwt = Jwt.withTokenValue("fake-token")
                .subject(userId.toString())
                .claim("realm_access", Map.of("roles", java.util.List.of("ADMIN")))
                .header("alg", "RS256")
                .build();

        userJwt = Jwt.withTokenValue("fake-token")
                .subject(userId.toString())
                .claim("realm_access", Map.of("roles", java.util.List.of("TECHNICIAN")))
                .header("alg", "RS256")
                .build();

        var bucket = mock(Bucket.class);
        when(bucket.tryConsumeAndReturnRemaining(1))
                .thenReturn(ConsumptionProbe.consumed(100L, 60_000_000_000L));
        when(rateLimitConfig.tryConsume(anyString(), anyInt()))
                .thenReturn(ConsumptionProbe.consumed(100L, 60_000_000_000L));
    }

    @Nested
    @DisplayName("POST /api/admin/users")
    class CreateUser {
        @Test
        @DisplayName("Cree un utilisateur avec le role ADMIN")
        void createUser_asAdmin_returns201() throws Exception {
            var request = new CreateUserRequest("newuser", "new@ngstars.tg",
                    "New", "User", "Password1!@#", "TECHNICIAN", null);
            when(userService.createUser(any(), anyString(), isNull())).thenReturn(userResponse);

            mockMvc.perform(post("/api/admin/users")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                    .jwt(j -> j.subject(userId.toString())))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.username").value("testuser"));
        }

        @Test
        @DisplayName("Rejette si non ADMIN")
        void createUser_asTechnician_returns403() throws Exception {
            var request = new CreateUserRequest("newuser", "new@ngstars.tg",
                    "New", "User", "Password1!@#", "TECHNICIAN", null);

            mockMvc.perform(post("/api/admin/users")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_TECHNICIAN"))
                                    .jwt(j -> j.subject(userId.toString())))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/admin/users")
    class GetUsers {
        @Test
        @DisplayName("Retourne la page des utilisateurs")
        void getUsers_asAdmin_returns200() throws Exception {
            when(userService.getAllUsers(any())).thenReturn(new PageImpl<>(java.util.List.of(userResponse)));

            mockMvc.perform(get("/api/admin/users")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].username").value("testuser"));
        }

        @Test
        @DisplayName("Rejette si non ADMIN")
        void getUsers_asTechnician_returns403() throws Exception {
            mockMvc.perform(get("/api/admin/users")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_TECHNICIAN"))))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/admin/users/{id}")
    class GetUserById {
        @Test
        @DisplayName("Retourne l'utilisateur par ID")
        void getUser_asAdmin_returns200() throws Exception {
            when(userService.getUser(userId)).thenReturn(userResponse);

            mockMvc.perform(get("/api/admin/users/" + userId)
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(userId.toString()));
        }
    }

    @Nested
    @DisplayName("DELETE /api/admin/users/{id}")
    class DeleteUser {
        @Test
        @DisplayName("Supprime un utilisateur")
        void deleteUser_asAdmin_returns204() throws Exception {
            doNothing().when(userService).deleteUser(any(), anyString());

            mockMvc.perform(delete("/api/admin/users/" + userId)
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                    .jwt(j -> j.subject(userId.toString())))
                            .with(csrf()))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("GET /api/users/me")
    class GetProfile {
        @Test
        @DisplayName("Retourne le profil de l'utilisateur connecte")
        void getProfile_returns200() throws Exception {
            when(userService.getProfile(userId)).thenReturn(userResponse);

            mockMvc.perform(get("/api/users/me")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_TECHNICIAN"))
                                    .jwt(j -> j.subject(userId.toString()))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("testuser"));
        }
    }

    @Nested
    @DisplayName("POST /api/users/me/change-password")
    class ChangePassword {
        @Test
        @DisplayName("Change le mot de passe avec succes")
        void changePassword_returns200() throws Exception {
            var request = new ChangePasswordRequest("OldPass1!@", "NewPass1!@");
            when(bruteForceProtection.isLockedOut(anyString())).thenReturn(false);
            doNothing().when(userService).changePassword(any(), any());

            mockMvc.perform(post("/api/users/me/change-password")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_TECHNICIAN"))
                                    .jwt(j -> j.subject(userId.toString())
                                            .claim("preferred_username", "testuser")))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Password changed successfully"));
        }

        @Test
        @DisplayName("Rejette si le compte est locke")
        void changePassword_lockedOut_returns429() throws Exception {
            var request = new ChangePasswordRequest("OldPass1!@", "NewPass1!@");
            when(bruteForceProtection.isLockedOut(anyString())).thenReturn(true);

            mockMvc.perform(post("/api/users/me/change-password")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_TECHNICIAN"))
                                    .jwt(j -> j.subject(userId.toString())
                                            .claim("preferred_username", "testuser")))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isTooManyRequests());
        }
    }

    @Nested
    @DisplayName("POST /api/public/register")
    class Register {
        @Test
        @DisplayName("Inscrit un nouveau client")
        void register_returns201() throws Exception {
            var request = new CreateUserRequest("client1", "client@ngstars.tg",
                    "Client", "User", "Password1!@#", "CLIENT_USER", null);
            when(userService.registerClient(any(), anyString())).thenReturn(userResponse);
            when(emailVerificationService.generateVerificationLink(any(), anyString())).thenReturn("https://verify.link/token");
            doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString());

            mockMvc.perform(post("/api/public/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").exists());
        }
    }

    @Nested
    @DisplayName("GET /api/public/verify-email")
    class VerifyEmail {
        @Test
        @DisplayName("Verifie un email valide")
        void verifyEmail_validToken_returns200() throws Exception {
            var verificationResult = EmailVerificationService.VerificationResult.valid(userId, "test@ngstars.tg");
            when(emailVerificationService.verifyToken("valid-token")).thenReturn(verificationResult);
            doNothing().when(userService).markEmailVerified(any());

            mockMvc.perform(get("/api/public/verify-email")
                            .param("token", "valid-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Email address verified successfully"));
        }

        @Test
        @DisplayName("Rejette un token expire")
        void verifyEmail_expiredToken_returns400() throws Exception {
            var verificationResult = EmailVerificationService.VerificationResult.expired("Token expired");
            when(emailVerificationService.verifyToken("expired-token")).thenReturn(verificationResult);

            mockMvc.perform(get("/api/public/verify-email")
                            .param("token", "expired-token"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("expired"));
        }
    }
}
