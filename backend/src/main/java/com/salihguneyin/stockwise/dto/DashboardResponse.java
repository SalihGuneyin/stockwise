package com.salihguneyin.stockwise.dto;

import java.util.List;

public record DashboardResponse(
        List<SummaryCardResponse> summary,
        List<PipelineMetricResponse> pipeline,
        List<StockMovementResponse> recentMovements
) {
}
