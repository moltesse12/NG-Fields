package tg.ngstars.report.dto;

import java.math.BigDecimal;
import java.util.Map;

public record AnalyticsDto(
    long totalInterventions,
    Map<String, Long> statusCounts,
    long billableCount,
    long nonBillableCount,
    BigDecimal totalBillingAmount,
    Map<String, Long> equipmentTypeCounts,
    Map<String, Long> clientCounts,
    double averageBillingAmount
) {}
