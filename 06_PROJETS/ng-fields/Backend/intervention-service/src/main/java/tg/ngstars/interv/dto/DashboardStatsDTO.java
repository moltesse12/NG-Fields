package tg.ngstars.interv.dto;

import java.util.Map;

public record DashboardStatsDTO(
    long totalInterventions,
    long totalCompleted,
    long totalPending,
    long totalInProgress,
    long totalCancelled,
    long totalAssigned,
    Map<String, Long> countByStatus,
    Double averageDurationMinutes,
    java.math.BigDecimal totalEstimatedCost,
    java.math.BigDecimal totalActualCost,
    long totalClients,
    long totalTechnicians
) {}
