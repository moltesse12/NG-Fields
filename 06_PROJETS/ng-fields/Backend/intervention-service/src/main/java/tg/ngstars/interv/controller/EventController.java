package tg.ngstars.interv.controller;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import tg.ngstars.interv.service.SecurityUtils;
import tg.ngstars.interv.service.SseEmitterManager;

@RestController
@RequestMapping("/api/events")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
@Tag(name = "Events SSE", description = "Server-Sent Events pour le temps reel des interventions")
public class EventController {

    private final SseEmitterManager sseManager;
    private final SecurityUtils securityUtils;

    public EventController(SseEmitterManager sseManager, SecurityUtils securityUtils) {
        this.sseManager = sseManager;
        this.securityUtils = securityUtils;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Stream SSE des evenements d'interventions", description = "Connecte un client au flux temps reel. Recoit les evenements : INTERVENTION_STATUS_CHANGED, INTERVENTION_ASSIGNED, INTERVENTION_CREATED, INTERVENTION_DELETED.")
    @ApiResponse(responseCode = "200", description = "Stream SSE ouvert")
    public SseEmitter stream() {
        var userId = securityUtils.getCurrentUserId();
        return sseManager.createEmitter(userId);
    }

    @GetMapping("/connected")
    @Operation(summary = "Nombre de clients SSE connectes")
    @ApiResponse(responseCode = "200", description = "Nombre retourne")
    public ResponseEntity<java.util.Map<String, Object>> connectedCount() {
        return ResponseEntity.ok(java.util.Map.of(
                "connected", sseManager.getConnectedCount()));
    }
}
