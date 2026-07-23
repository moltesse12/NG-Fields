package tg.ngstars.interv.service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseEmitterManager {

    private static final Logger log = LoggerFactory.getLogger(SseEmitterManager.class);

    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(UUID userId) {
        var emitter = new SseEmitter(30 * 60 * 1000L);
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> {
            emitters.remove(userId);
            log.debug("SSE completion pour userId={}", userId);
        });
        emitter.onTimeout(() -> {
            emitters.remove(userId);
            log.debug("SSE timeout pour userId={}", userId);
        });
        emitter.onError(e -> {
            emitters.remove(userId);
            log.debug("SSE error pour userId={}: {}", userId, e.getMessage());
        });
        log.debug("SSE connecte pour userId={}, total={}", userId, emitters.size());
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
                log.debug("SSE envoi echoue pour userId={}: {}", entry.getKey(), e.getMessage());
            }
        }
        dead.forEach(emitters::remove);
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
                log.debug("SSE envoi echoue pour userId={}: {}", userId, e.getMessage());
            }
        }
    }

    public int getConnectedCount() {
        return emitters.size();
    }
}
