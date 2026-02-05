package com.project2.service1.service;

import com.project2.service1.client.EnrichmentClient;
import com.project2.service1.dto.EnrichmentResponse;
import com.project2.service1.dto.IncomingEvent;
import com.project2.service1.dto.ModeratedEvent;
import com.project2.service1.producer.Topic2Producer;
import com.project2.service1.repository.ActiveCaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModerationService {
    private static final String ACTIVE_STATUS = "ACTIVE";

    private final IdempotencyService idempotencyService;
    private final ActiveCaseRepository activeCaseRepository;
    private final EnrichmentClient enrichmentClient;
    private final WorkingHoursService workingHoursService;
    private final Topic2Producer topic2Producer;

    public void process(IncomingEvent event){
        if(!idempotencyService.markIfFirstTime(event.eventId())){
            log.info("Idempotency check failed:{}", event.eventId());
            return;
        }

        if(activeCaseRepository.existsByClientIdAndCategoryAndStatus(event.clientId(),
                event.category(),
                ACTIVE_STATUS)){
            log.info("Already active case for: eventId={} category={}", event.eventId(), event.category());
            return;
        }

        EnrichmentResponse enrichment = enrichmentClient.getByCategory(event.category());
        if(Boolean.FALSE.equals(enrichment.allowedOutsideWorkingHours())
        && !workingHoursService.isWorkingHours(event.createdAt())){
            log.info("Working hours not allowed, eventId={}, category={}", event.eventId(), event.category());
            return;
        }

        ModeratedEvent outEvent = new ModeratedEvent(
                event.eventId(), event.clientId(),
                event.caseId(), event.category(),
                event.createdAt(), "APPROVED", "PASSED_ALL_RULES"
        );

        topic2Producer.publish(outEvent);

    }
}
