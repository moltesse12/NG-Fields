package tg.ngstars.interv.service;

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

import java.io.IOException;
import java.util.UUID;

@Service
public class SignatureService {

    private static final Logger log = LoggerFactory.getLogger(SignatureService.class);

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

    // ponytail: upload first, then save; cleanup media if save fails
    @Transactional
    public String signClient(UUID interventionId, SignatureRequest req) throws IOException {
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

    // ponytail: upload first, then save; cleanup media if save fails
    @Transactional
    public String signTechnician(UUID interventionId, SignatureRequest req) throws IOException {
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

    // ponytail: upload first, then save; cleanup media if save fails
    @Transactional
    public String signManager(UUID interventionId, SignatureRequest req) throws IOException {
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

    private static String extractFilename(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    private Intervention findOrThrow(UUID id) {
        return interventionRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Intervention not found: " + id));
    }
}
