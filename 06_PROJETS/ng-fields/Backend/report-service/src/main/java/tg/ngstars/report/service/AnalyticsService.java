package tg.ngstars.report.service;

import org.springframework.stereotype.Service;
import tg.ngstars.report.client.InterventionClient;
import tg.ngstars.report.dto.AnalyticsDto;
import tg.ngstars.report.dto.InterventionReportDto;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final InterventionClient interventionClient;

    public AnalyticsService(InterventionClient interventionClient) {
        this.interventionClient = interventionClient;
    }

    public AnalyticsDto getAnalytics() {
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

        return new AnalyticsDto(
                interventions.size(),
                statusCounts,
                equipmentTypeCounts,
                clientCounts
        );
    }
}
