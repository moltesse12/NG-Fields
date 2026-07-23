package tg.ngstars.interv.dto;

import java.math.BigDecimal;
import java.util.Map;

public record InterventionStatsResponse(
    long totalInterventions,
    long activeInterventions,
    Map<String, Long> countByStatus,
    Long totalAssigned,
    Long totalCompleted,
    Long totalPending,
    Long totalCancelled,
    Double averageDurationMinutes,
    BigDecimal estimatedRevenue
) {}
