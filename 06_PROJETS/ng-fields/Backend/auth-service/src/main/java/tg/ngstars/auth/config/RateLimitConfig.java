package tg.ngstars.auth.config;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;

@Configuration
public class RateLimitConfig {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Bean
    public ConcurrentHashMap<String, Bucket> rateLimitBuckets() {
        return buckets;
    }

    public Bucket getOrCreateBucket(String key, int limitPerMinute) {
        return buckets.computeIfAbsent(key, k -> createBucket(limitPerMinute));
    }

    public ConsumptionProbe tryConsume(String key, int limitPerMinute) {
        Bucket bucket = getOrCreateBucket(key, limitPerMinute);
        return bucket.tryConsumeAndReturnRemaining(1);
    }

    private Bucket createBucket(int limitPerMinute) {
        Bandwidth limit = Bandwidth.builder()
            .capacity(limitPerMinute)
            .refillGreedy(limitPerMinute, Duration.ofMinutes(1))
            .build();
        return Bucket.builder().addLimit(limit).build();
    }
}
