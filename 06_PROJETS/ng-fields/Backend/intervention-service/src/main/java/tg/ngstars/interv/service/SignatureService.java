package tg.ngstars.interv.service;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.ngstars.interv.client.MediaClient;
import tg.ngstars.interv.dto.SignatureRequest;
import tg.ngstars.common.exception.ConflictException;
import tg.ngstars.common.exception.ForbiddenException;
import tg.ngstars.common.exception.NotFoundException;
import tg.ngstars.interv.model.Intervention;
import tg.ngstars.interv.model.InterventionStatus;
import tg.ngstars.interv.repository.InterventionRepository;

@Service
public class SignatureService {

    private static final Logger log = LoggerFactory.getLogger(SignatureService.class);

    private static final java.util.Set<String> VALID_IMAGE_PREFIXES = java.util.Set.of(
            "/9j/",   // JPEG
            "iVBOR",  // PNG
            "R0lGO",  // GIF
            "UklGR"   // WEBP
    );

    private final InterventionRepository interventionRepo;
    private final MediaClient mediaClient;
    private final SecurityUtils securityUtils;

    public SignatureService(
            InterventionRepository interventionRepo,
            MediaClient mediaClient,
            SecurityUtils securityUtils) {
        this.interventionRepo = interventionRepo;
        this.mediaClient      = mediaClient;
        this.securityUtils    = securityUtils;
    }

    @Transactional
    public String signClient(UUID interventionId, SignatureRequest req) throws IOException {
        validateSignature(req.imageBase64());
        var i = findOrThrow(interventionId);
        if (!securityUtils.isAdminOrManager() && (i.getAssignedTo() == null || !i.getAssignedTo().equals(securityUtils.getCurrentUserId())))
            throw new ForbiddenException("Not assigned to this intervention");
        String url = mediaClient.uploadBase64(req.imageBase64());
        i.setClientSignature(url);
        try {
            interventionRepo.save(i);
        } catch (Exception e) {
            mediaClient.deleteFile(extractFilename(url));
            throw e;
        }
        log.info("Signature CLIENT enregistrée — intervention {}", interventionId);
        return url;
    }

    @Transactional
    public String signTechnician(UUID interventionId, SignatureRequest req) throws IOException {
        validateSignature(req.imageBase64());
        var i = findOrThrow(interventionId);
        if (!securityUtils.isAdminOrManager() && (i.getAssignedTo() == null || !i.getAssignedTo().equals(securityUtils.getCurrentUserId())))
            throw new ForbiddenException("Not assigned to this intervention");
        String url = mediaClient.uploadBase64(req.imageBase64());
        i.setTechnicianSignature(url);
        try {
            interventionRepo.save(i);
        } catch (Exception e) {
            mediaClient.deleteFile(extractFilename(url));
            throw e;
        }
        log.info("Signature TECHNICIEN enregistrée — intervention {}", interventionId);
        return url;
    }

    @Transactional
    public String signManager(UUID interventionId, SignatureRequest req) throws IOException {
        validateSignature(req.imageBase64());
        var i = findOrThrow(interventionId);
        if (!securityUtils.isAdminOrManager() && (i.getAssignedTo() == null || !i.getAssignedTo().equals(securityUtils.getCurrentUserId())))
            throw new ForbiddenException("Not assigned to this intervention");
        String url = mediaClient.uploadBase64(req.imageBase64());
        i.setManagerSignature(url);

        if (i.getClientSignature() != null
                && i.getTechnicianSignature() != null) {
            var currentEnum = InterventionStatus.fromString(i.getStatus());
            if (!currentEnum.canTransitionTo(InterventionStatus.COMPLETED.name())) {
                throw new ConflictException(
                        "Cannot complete intervention in status " + i.getStatus());
            }
            i.setStatus(InterventionStatus.COMPLETED.name());
            log.info("Intervention {} → COMPLETED (3 signatures présentes)", interventionId);
        }

        try {
            interventionRepo.save(i);
        } catch (Exception e) {
            mediaClient.deleteFile(extractFilename(url));
            throw e;
        }
        log.info("Signature MANAGER enregistrée — intervention {}", interventionId);
        return url;
    }

    private void validateSignature(String imageBase64) {
        if (imageBase64 == null || imageBase64.isBlank()) {
            throw new IllegalArgumentException("Signature image ne peut pas etre vide");
        }
        String data = imageBase64.replaceAll("^data:image/[^;]+;base64,", "");
        try {
            Base64.getDecoder().decode(data);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Signature image n'est pas un Base64 valide");
        }
        String prefix = data.length() >= 6 ? data.substring(0, 6) : data;
        if (VALID_IMAGE_PREFIXES.stream().noneMatch(prefix::startsWith)) {
            throw new IllegalArgumentException("Signature image n'est pas un format d'image reconnu (JPEG, PNG, GIF, WEBP)");
        }
    }

    private static String extractFilename(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    private Intervention findOrThrow(UUID id) {
        return interventionRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Intervention not found: " + id));
    }
}
