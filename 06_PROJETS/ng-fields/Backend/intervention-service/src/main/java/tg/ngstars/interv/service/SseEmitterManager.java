package tg.ngstars.interv.service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseEmitterManager {

    private static final Logger log = LoggerFactory.getLogger(SseEmitterManager.class);

    private static final long EMITTER_TIMEOUT_MS = 60 * 60 * 1000L;
    private static final long STALE_CHECK_INTERVAL_SECONDS = 300;
    private static final long STALE_THRESHOLD_MS = 5 * 60 * 1000L;

    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<UUID, Long> emitterCreationTime = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        var t = new Thread(r, "sse-cleanup");
        t.setDaemon(true);
        return t;
    });

    @PostConstruct
    public void init() {
        cleanupExecutor.scheduleAtFixedRate(this::cleanupStaleEmitters,
                STALE_CHECK_INTERVAL_SECONDS, STALE_CHECK_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destroy() {
        cleanupExecutor.shutdownNow();
    }

    public SseEmitter createEmitter(UUID userId) {
        var existing = emitters.remove(userId);
        if (existing != null) {
            existing.complete();
            emitterCreationTime.remove(userId);
        }

        var emitter = new SseEmitter(EMITTER_TIMEOUT_MS);
        emitters.put(userId, emitter);
        emitterCreationTime.put(userId, System.currentTimeMillis());

        emitter.onCompletion(() -> {
            emitters.remove(userId);
            emitterCreationTime.remove(userId);
            log.debug("SSE completion for userId={}", userId);
        });
        emitter.onTimeout(() -> {
            emitters.remove(userId);
            emitterCreationTime.remove(userId);
            log.debug("SSE timeout for userId={}", userId);
        });
        emitter.onError(e -> {
            emitters.remove(userId);
            emitterCreationTime.remove(userId);
            log.debug("SSE error for userId={}: {}", userId, e.getMessage());
        });
        log.debug("SSE connected for userId={}, total={}", userId, emitters.size());
        return emitter;
    }

    public void sendEvent(String eventName, Object data) {
        var dead = new java.util.ArrayList<UUID>();
        for (var entry : emitters.entrySet()) {
            try {
                entry.getValue().send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
            } catch (IOException e) {
                dead.add(entry.getKey());
                log.debug("SSE send failed for userId={}: {}", entry.getKey(), e.getMessage());
            } catch (IllegalStateException e) {
                dead.add(entry.getKey());
            }
        }
        dead.forEach(emitters::remove);
        dead.forEach(emitterCreationTime::remove);
    }

    public void sendToUser(UUID userId, String eventName, Object data) {
        var emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
            } catch (IOException e) {
                emitters.remove(userId);
                emitterCreationTime.remove(userId);
                log.debug("SSE send failed for userId={}: {}", userId, e.getMessage());
            } catch (IllegalStateException e) {
                emitters.remove(userId);
                emitterCreationTime.remove(userId);
            }
        }
    }

    public int getConnectedCount() {
        return emitters.size();
    }

    private void cleanupStaleEmitters() {
        var now = System.currentTimeMillis();
        var stale = new java.util.ArrayList<UUID>();
        for (var entry : emitterCreationTime.entrySet()) {
            if (now - entry.getValue() > STALE_THRESHOLD_MS) {
                var emitter = emitters.get(entry.getKey());
                if (emitter != null) {
                    try {
                        emitter.send(SseEmitter.event().name("ping").data("keepalive"));
                    } catch (Exception e) {
                        stale.add(entry.getKey());
                    }
                }
            }
        }
        if (!stale.isEmpty()) {
            log.debug("Cleaning up {} stale SSE emitters", stale.size());
            stale.forEach(id -> {
                var emitter = emitters.remove(id);
                emitterCreationTime.remove(id);
                if (emitter != null) {
                    emitter.complete();
                }
            });
        }
    }
}
