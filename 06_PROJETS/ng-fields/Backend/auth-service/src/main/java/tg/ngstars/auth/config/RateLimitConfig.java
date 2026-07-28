package tg.ngstars.auth.config;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;

@Configuration
public class RateLimitConfig {

    private static final Logger log = LoggerFactory.getLogger(RateLimitConfig.class);
    private final ConcurrentHashMap<String, BucketEntry> buckets = new ConcurrentHashMap<>();

    public Bucket getOrCreateBucket(String key, int limitPerMinute) {
        return buckets.compute(key, (k, existing) -> {
            if (existing != null) return existing;
            return new BucketEntry(createBucket(limitPerMinute), Instant.now());
        }).bucket();
    }

    public ConsumptionProbe tryConsume(String key, int limitPerMinute) {
        Bucket bucket = getOrCreateBucket(key, limitPerMinute);
        return bucket.tryConsumeAndReturnRemaining(1);
    }

    @org.springframework.scheduling.annotation.Scheduled(fixedDelay = 300_000, initialDelay = 300_000)
    public void evictExpiredBuckets() {
        var cutoff = Instant.now().minus(Duration.ofMinutes(10));
        int removed = 0;
        var iterator = buckets.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (entry.getValue().createdAt().isBefore(cutoff)) {
                iterator.remove();
                removed++;
            }
        }
        if (removed > 0) {
            log.debug("Evicted {} expired rate-limit buckets", removed);
        }
    }

    private Bucket createBucket(int limitPerMinute) {
        Bandwidth limit = Bandwidth.builder()
            .capacity(limitPerMinute)
            .refillGreedy(limitPerMinute, Duration.ofMinutes(1))
            .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private record BucketEntry(Bucket bucket, Instant createdAt) {}
}
