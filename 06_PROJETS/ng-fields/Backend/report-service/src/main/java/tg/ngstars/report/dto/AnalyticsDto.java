package tg.ngstars.report.dto;

import java.util.Map;

public record AnalyticsDto(
    long totalInterventions,
    Map<String, Long> statusCounts,
    Map<String, Long> equipmentTypeCounts,
    Map<String, Long> clientCounts
) {}
