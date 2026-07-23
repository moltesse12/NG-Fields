package tg.ngstars.notification.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RateLimiter {

    private static final Logger log = LoggerFactory.getLogger(RateLimiter.class);

    private final ConcurrentHashMap<String, RateBucket> buckets = new ConcurrentHashMap<>();

    public boolean tryAcquire(String key, int maxPerMinute) {
        var bucket = buckets.compute(key, (k, existing) -> {
            if (existing == null || existing.isExpired()) {
                return new RateBucket(maxPerMinute);
            }
            return existing;
        });

        if (bucket.tryAcquire()) {
            return true;
        }

        log.warn("Rate limit dépassé pour key={}, max={}/min", key, maxPerMinute);
        return false;
    }

    public int getRemaining(String key, int maxPerMinute) {
        var bucket = buckets.get(key);
        if (bucket == null || bucket.isExpired()) return maxPerMinute;
        return Math.max(0, maxPerMinute - bucket.count.get());
    }

    private static class RateBucket {
        private final int maxPerMinute;
        private final long createdAt = System.currentTimeMillis();
        private final AtomicInteger count = new AtomicInteger(0);

        RateBucket(int maxPerMinute) {
            this.maxPerMinute = maxPerMinute;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - createdAt > 60_000;
        }

        boolean tryAcquire() {
            if (isExpired()) return true;
            return count.incrementAndGet() <= maxPerMinute;
        }
    }
}
