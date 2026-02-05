package com.project2.service1.dto;

import java.time.Instant;

public record ModeratedEvent(
        String eventId,
        String clientId,
        String caseId,
        String category,
        Instant createdAt,
        String moderationResult,
        String reason
) {
}