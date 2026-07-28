package tg.ngstars.report.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tg.ngstars.report.dto.CreatePdfTemplateRequest;
import tg.ngstars.report.dto.UpdatePdfTemplateRequest;
import tg.ngstars.report.model.PdfTemplate;
import tg.ngstars.report.repository.PdfTemplateRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PdfTemplateService")
class PdfTemplateServiceTest {

    @Mock
    private PdfTemplateRepository repository;

    @InjectMocks
    private PdfTemplateService service;

    private PdfTemplate sampleTemplate;

    @BeforeEach
    void setUp() {
        sampleTemplate = new PdfTemplate();
        sampleTemplate.setId(UUID.randomUUID());
        sampleTemplate.setName("Template A");
        sampleTemplate.setDescription("Description A");
        sampleTemplate.setTemplateType("INTERVENTION_REPORT");
        sampleTemplate.setConfig("{\"orientation\":\"LANDSCAPE\"}");
        sampleTemplate.setIsDefault(false);
        sampleTemplate.setCreatedBy("user-123");
    }

    @Nested
    @DisplayName("listAll(templateType)")
    class ListAll {

        @Test
        @DisplayName("Retourne les templates filtres par type")
        void retourneTemplatesFiltres() {
            when(repository.findByTemplateTypeOrderByIsDefaultDescNameAsc("INTERVENTION_REPORT"))
                .thenReturn(List.of(sampleTemplate));

            var result = service.listAll("INTERVENTION_REPORT");

            assertEquals(1, result.size());
            assertEquals("Template A", result.get(0).name());
        }

        @Test
        @DisplayName("Retourne tous les templates si type est null")
        void retourneTousSiTypeNull() {
            when(repository.findAll()).thenReturn(List.of(sampleTemplate));

            var result = service.listAll(null);

            assertEquals(1, result.size());
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

            assertEquals("Template A", result.name());
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
    @DisplayName("getDefault(templateType)")
    class GetDefault {

        @Test
        @DisplayName("Retourne le template par defaut")
        void retourneTemplateParDefaut() {
            when(repository.findByIsDefaultTrueAndTemplateType("INTERVENTION_REPORT"))
                .thenReturn(Optional.of(sampleTemplate));

            var result = service.getDefault("INTERVENTION_REPORT");

            assertNotNull(result);
        }

        @Test
        @DisplayName("Lance exception si aucun template par defaut")
        void lanceExceptionSiAucunParDefaut() {
            when(repository.findByIsDefaultTrueAndTemplateType(any())).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                () -> service.getDefault("INTERVENTION_REPORT"));
        }
    }

    @Nested
    @DisplayName("create(request, userKeycloakId)")
    class Create {

        @Test
        @DisplayName("Cree un template avec succes")
        void creeTemplateAvecSucces() {
            var request = new CreatePdfTemplateRequest("Nouveau", "Desc", null, "{\"orientation\":\"PORTRAIT\"}");
            when(repository.save(any(PdfTemplate.class))).thenAnswer(invocation -> {
                var t = invocation.getArgument(0, PdfTemplate.class);
                t.setId(UUID.randomUUID());
                return t;
            });

            var result = service.create(request, "user-123");

            assertEquals("Nouveau", result.name());
            verify(repository).save(any(PdfTemplate.class));
        }

        @Test
        @DisplayName("Sanitise le nom et la description")
        void sanitiseNomEtDescription() {
            var request = new CreatePdfTemplateRequest("<script>alert(1)</script> Nom", "Desc <b>gras</b>", null, "{}");
            when(repository.save(any(PdfTemplate.class))).thenAnswer(invocation -> {
                var t = invocation.getArgument(0, PdfTemplate.class);
                t.setId(UUID.randomUUID());
                return t;
            });

            var result = service.create(request, "user-123");

            assertFalse(result.name().contains("<script>"));
        }

        @Test
        @DisplayName("Valide le JSON de config")
        void valideJsonConfig() {
            var request = new CreatePdfTemplateRequest("Test", null, null, "invalid json {{{");
            when(repository.save(any(PdfTemplate.class))).thenAnswer(invocation -> {
                var t = invocation.getArgument(0, PdfTemplate.class);
                t.setId(UUID.randomUUID());
                return t;
            });

            var result = service.create(request, "user-123");

            assertEquals("{}", result.config());
        }
    }

    @Nested
    @DisplayName("update(id, request)")
    class Update {

        @Test
        @DisplayName("Met a jour le template")
        void metAJourTemplate() {
            when(repository.findById(sampleTemplate.getId())).thenReturn(Optional.of(sampleTemplate));
            when(repository.save(any(PdfTemplate.class))).thenAnswer(inv -> inv.getArgument(0));

            var request = new UpdatePdfTemplateRequest("Nom Modifie", null, null, null);
            var result = service.update(sampleTemplate.getId(), request);

            assertEquals("Nom Modifie", result.name());
        }

        @Test
        @DisplayName("Definit comme defaut et efface les autres")
        void definitCommeDefaut() {
            sampleTemplate.setTemplateType("INTERVENTION_REPORT");
            when(repository.findById(sampleTemplate.getId())).thenReturn(Optional.of(sampleTemplate));
            when(repository.findByIsDefaultTrueAndTemplateType("INTERVENTION_REPORT"))
                .thenReturn(Optional.empty());
            when(repository.save(any(PdfTemplate.class))).thenAnswer(inv -> inv.getArgument(0));

            var request = new UpdatePdfTemplateRequest(null, null, null, true);
            var result = service.update(sampleTemplate.getId(), request);

            assertTrue(result.isDefault());
        }

        @Test
        @DisplayName("Lance exception si template introuvable")
        void lanceExceptionSiIntrouvable() {
            when(repository.findById(any())).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                () -> service.update(UUID.randomUUID(), new UpdatePdfTemplateRequest(null, null, null, null)));
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
