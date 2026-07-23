package tg.ngstars.interv.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tg.ngstars.interv.dto.DashboardStatsDTO;
import tg.ngstars.interv.repository.InterventionRepository;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final InterventionRepository interventionRepository;

    public DashboardService(InterventionRepository interventionRepository) {
        this.interventionRepository = interventionRepository;
    }

    public DashboardStatsDTO getManagerDashboard() {
        var countByStatus = interventionRepository.countByStatus().stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1],
                        (a, b) -> a,
                        LinkedHashMap::new));

        long inProgress = countByStatus.getOrDefault("IN_PROGRESS", 0L);

        return new DashboardStatsDTO(
                interventionRepository.countActive(),
                interventionRepository.countCompleted(),
                interventionRepository.countPending(),
                inProgress,
                interventionRepository.countCancelled(),
                interventionRepository.countAssigned(),
                countByStatus,
                interventionRepository.averageDurationMinutes(),
                interventionRepository.sumEstimatedCost(),
                interventionRepository.sumTotalCost(),
                interventionRepository.countDistinctClients(),
                interventionRepository.countDistinctTechnicians()
        );
    }

    public DashboardStatsDTO getClientDashboard(java.util.UUID companyId) {
        var countByStatus = interventionRepository.countByClientId(companyId).stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1],
                        (a, b) -> a,
                        LinkedHashMap::new));

        long total = interventionRepository.countByClientIdTotal(companyId);
        long completed = countByStatus.getOrDefault("COMPLETED", 0L);
        long pending = countByStatus.getOrDefault("PENDING", 0L);
        long inProgress = countByStatus.getOrDefault("IN_PROGRESS", 0L);
        long cancelled = countByStatus.getOrDefault("CANCELLED", 0L);
        long assigned = interventionRepository.countAssignedByClientId(companyId);

        return new DashboardStatsDTO(
                total,
                completed,
                pending,
                inProgress,
                cancelled,
                assigned,
                countByStatus,
                interventionRepository.averageDurationByClientId(companyId),
                interventionRepository.sumEstimatedCostByClientId(companyId),
                interventionRepository.sumTotalCostByClientId(companyId),
                1,
                interventionRepository.countDistinctTechniciansByClientId(companyId)
        );
    }
}
