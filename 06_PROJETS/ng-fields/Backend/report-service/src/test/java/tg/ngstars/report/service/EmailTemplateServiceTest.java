package tg.ngstars.report.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tg.ngstars.report.dto.CreateEmailTemplateRequest;
import tg.ngstars.report.dto.UpdateEmailTemplateRequest;
import tg.ngstars.report.model.EmailTemplate;
import tg.ngstars.report.repository.EmailTemplateRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailTemplateService")
class EmailTemplateServiceTest {

    @Mock
    private EmailTemplateRepository repository;

    @InjectMocks
    private EmailTemplateService service;

    private EmailTemplate sampleTemplate;

    @BeforeEach
    void setUp() {
        sampleTemplate = new EmailTemplate();
        sampleTemplate.setId(UUID.randomUUID());
        sampleTemplate.setName("Email Welcome");
        sampleTemplate.setDescription("Email de bienvenue");
        sampleTemplate.setTemplateKey("welcome-email");
        sampleTemplate.setSubject("Bienvenue");
        sampleTemplate.setBodyHtml("<h1>Bienvenue</h1>");
        sampleTemplate.setIsActive(true);
        sampleTemplate.setCreatedBy("user-123");
    }

    @Nested
    @DisplayName("listAll()")
    class ListAll {

        @Test
        @DisplayName("Retourne tous les templates")
        void retourneTousLesTemplates() {
            when(repository.findAll()).thenReturn(List.of(sampleTemplate));

            var result = service.listAll();

            assertEquals(1, result.size());
            assertEquals("Email Welcome", result.get(0).name());
        }
    }

    @Nested
    @DisplayName("getById(id)")
    class GetById {

        @Test
        @DisplayName("Retourne le template par id")
        void retourneTemplateParId() {
            when(repository.findById(sampleTemplate.getId())).thenReturn(Optional.of(sampleTemplate));

            var result = service.getById(sampleTemplate.getId());

            assertEquals("welcome-email", result.templateKey());
        }

        @Test
        @DisplayName("Lance exception si introuvable")
        void lanceExceptionSiIntrouvable() {
            when(repository.findById(any())).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                () -> service.getById(UUID.randomUUID()));
        }
    }

    @Nested
    @DisplayName("getByKey(key)")
    class GetByKey {

        @Test
        @DisplayName("Retourne le template par cle")
        void retourneTemplateParCle() {
            when(repository.findByTemplateKey("welcome-email")).thenReturn(Optional.of(sampleTemplate));

            var result = service.getByKey("welcome-email");

            assertEquals("Email Welcome", result.name());
        }

        @Test
        @DisplayName("Lance exception si cle introuvable")
        void lanceExceptionSiCleIntrouvable() {
            when(repository.findByTemplateKey(any())).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                () -> service.getByKey("unknown-key"));
        }
    }

    @Nested
    @DisplayName("create(request, userKeycloakId)")
    class Create {

        @Test
        @DisplayName("Cree un template avec succes")
        void creeTemplateAvecSucces() {
            var request = new CreateEmailTemplateRequest("Nouveau Email", "Desc", "new-key", "Sujet", "<p>Corps</p>");
            when(repository.save(any(EmailTemplate.class))).thenAnswer(invocation -> {
                var t = invocation.getArgument(0, EmailTemplate.class);
                t.setId(UUID.randomUUID());
                return t;
            });

            var result = service.create(request, "user-123");

            assertEquals("Nouveau Email", result.name());
            assertEquals("new-key", result.templateKey());
            verify(repository).save(any(EmailTemplate.class));
        }

        @Test
        @DisplayName("Sanitise le bodyHtml")
        void sanitiseBodyHtml() {
            var request = new CreateEmailTemplateRequest("Test", null, "key", "Sujet",
                "<h1>OK</h1><script>alert(1)</script>");
            when(repository.save(any(EmailTemplate.class))).thenAnswer(invocation -> {
                var t = invocation.getArgument(0, EmailTemplate.class);
                t.setId(UUID.randomUUID());
                return t;
            });

            var result = service.create(request, "user-123");

            assertFalse(result.bodyHtml().contains("<script>"));
            assertTrue(result.bodyHtml().contains("<h1>OK</h1>"));
        }
    }

    @Nested
    @DisplayName("update(id, request)")
    class Update {

        @Test
        @DisplayName("Met a jour le template")
        void metAJourTemplate() {
            when(repository.findById(sampleTemplate.getId())).thenReturn(Optional.of(sampleTemplate));
            when(repository.save(any(EmailTemplate.class))).thenAnswer(inv -> inv.getArgument(0));

            var request = new UpdateEmailTemplateRequest("Nom Modifie", null, null, null, null);
            var result = service.update(sampleTemplate.getId(), request);

            assertEquals("Nom Modifie", result.name());
        }

        @Test
        @DisplayName("Met a jour le bodyHtml avec sanitisation")
        void metAJourBodyHtml() {
            when(repository.findById(sampleTemplate.getId())).thenReturn(Optional.of(sampleTemplate));
            when(repository.save(any(EmailTemplate.class))).thenAnswer(inv -> inv.getArgument(0));

            var request = new UpdateEmailTemplateRequest(null, null, null, "<p>OK</p><script>xss</script>", null);
            var result = service.update(sampleTemplate.getId(), request);

            assertFalse(result.bodyHtml().contains("<script>"));
        }

        @Test
        @DisplayName("Desactive le template")
        void desactiveTemplate() {
            when(repository.findById(sampleTemplate.getId())).thenReturn(Optional.of(sampleTemplate));
            when(repository.save(any(EmailTemplate.class))).thenAnswer(inv -> inv.getArgument(0));

            var request = new UpdateEmailTemplateRequest(null, null, null, null, false);
            var result = service.update(sampleTemplate.getId(), request);

            assertFalse(result.isActive());
        }

        @Test
        @DisplayName("Lance exception si template introuvable")
        void lanceExceptionSiIntrouvable() {
            when(repository.findById(any())).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                () -> service.update(UUID.randomUUID(), new UpdateEmailTemplateRequest(null, null, null, null, null)));
        }
    }

    @Nested
    @DisplayName("delete(id)")
    class Delete {

        @Test
        @DisplayName("Supprime le template")
        void supprimeTemplate() {
            when(repository.existsById(sampleTemplate.getId())).thenReturn(true);

            assertDoesNotThrow(() -> service.delete(sampleTemplate.getId()));
            verify(repository).deleteById(sampleTemplate.getId());
        }

        @Test
        @DisplayName("Lance exception si template introuvable")
        void lanceExceptionSiIntrouvable() {
            when(repository.existsById(any())).thenReturn(false);

            assertThrows(IllegalArgumentException.class,
                () -> service.delete(UUID.randomUUID()));
        }
    }
}
