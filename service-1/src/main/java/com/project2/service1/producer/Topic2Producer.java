package com.project2.service1.producer;

import com.project2.service1.dto.ModeratedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Topic2Producer {
    private final KafkaTemplate<String, ModeratedEvent> kafkaTemplate;
    @Value("${app.kafka.topic-out}")
    private String topicOut;

    public void publish(ModeratedEvent moderatedEvent) {
        kafkaTemplate.send(topicOut, moderatedEvent.eventId(), moderatedEvent);
    }
}
