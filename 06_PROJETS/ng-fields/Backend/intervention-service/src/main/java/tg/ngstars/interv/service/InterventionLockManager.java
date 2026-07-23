package tg.ngstars.interv.service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InterventionLockManager {

    private static final Logger log = LoggerFactory.getLogger(InterventionLockManager.class);

    private final ConcurrentHashMap<UUID, ReentrantLock> locks = new ConcurrentHashMap<>();

    public ReentrantLock getLock(UUID interventionId) {
        return locks.computeIfAbsent(interventionId, id -> new ReentrantLock());
    }

    public void lock(UUID interventionId) {
        getLock(interventionId).lock();
        log.debug("Lock acquis pour intervention {}", interventionId);
    }

    public void unlock(UUID interventionId) {
        var lock = locks.get(interventionId);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
            log.debug("Lock libéré pour intervention {}", interventionId);
        }
    }

    public void cleanup(UUID interventionId) {
        var lock = locks.remove(interventionId);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
