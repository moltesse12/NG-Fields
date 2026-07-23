package tg.ngstars.report.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tg.ngstars.report.client.InterventionClient;
import tg.ngstars.report.dto.AnalyticsDto;
import tg.ngstars.report.dto.InterventionReportDto;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);

    private final InterventionClient interventionClient;
    private final AtomicReference<AnalyticsDto> cache = new AtomicReference<>();
    private volatile long lastRefresh = 0;
    private static final long CACHE_TTL_MS = 60_000;

    public AnalyticsService(InterventionClient interventionClient) {
        this.interventionClient = interventionClient;
    }

    public AnalyticsDto getAnalytics() {
        var cached = cache.get();
        if (cached != null && (System.currentTimeMillis() - lastRefresh) < CACHE_TTL_MS) {
            return cached;
        }
        return refreshCache();
    }

    @Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
    public void scheduledRefresh() {
        try {
            refreshCache();
        } catch (Exception e) {
            log.warn("Scheduled analytics cache refresh failed: {}", e.getMessage());
        }
    }

    private synchronized AnalyticsDto refreshCache() {
        var current = cache.get();
        if (current != null && (System.currentTimeMillis() - lastRefresh) < CACHE_TTL_MS) {
            return current;
        }

        log.debug("Refreshing analytics cache");
        var interventions = interventionClient.fetchAllForReport(10_000);

        var statusCounts = interventions.stream()
                .collect(Collectors.groupingBy(
                        i -> i.status() != null ? i.status() : "UNKNOWN",
                        Collectors.counting()));

        var equipmentTypeCounts = interventions.stream()
                .filter(i -> i.equipmentType() != null)
                .collect(Collectors.groupingBy(
                        InterventionReportDto::equipmentType,
                        Collectors.counting()));

        var clientCounts = interventions.stream()
                .filter(i -> i.clientName() != null)
                .collect(Collectors.groupingBy(
                        InterventionReportDto::clientName,
                        Collectors.counting()));

        var dto = new AnalyticsDto(
                interventions.size(),
                statusCounts,
                equipmentTypeCounts,
                clientCounts
        );

        cache.set(dto);
        lastRefresh = System.currentTimeMillis();
        return dto;
    }
}
