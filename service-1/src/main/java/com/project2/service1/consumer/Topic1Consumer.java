package com.project2.service1.consumer;

import com.project2.service1.dto.IncomingEvent;
import com.project2.service1.service.ModerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class Topic1Consumer {
    private final ModerationService moderationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topic-in}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String payload, Acknowledgment ack) {
        try {
            IncomingEvent event = objectMapper.readValue(payload, IncomingEvent.class);
            moderationService.process(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process payload={}", payload, e);
            throw new IllegalStateException("Failed to process kafka message", e);
        }
    }
}
