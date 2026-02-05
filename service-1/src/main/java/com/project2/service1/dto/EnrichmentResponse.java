package com.project2.service1.dto;

public record EnrichmentResponse(
        String category,
        Boolean allowedOutsideWorkingHours,
        Integer priority
) {
}