package com.project2.service2.Service;

import com.project2.service2.DTO.EnrichmentData;
import com.project2.service2.DTO.EnrichmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EnrichmentService {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public Mono<EnrichmentResponse> getEnrichment(String category) {
        String key = "category:" + category;

        return redisTemplate.opsForValue()
                .get(key)
                .flatMap(raw -> {
                    try {
                        EnrichmentData rule = objectMapper.readValue(raw, EnrichmentData.class);
                        return Mono.just(EnrichmentResponse.builder()
                                .category(rule.getCategory())
                                .allowedOutsideWorkingHours(rule.getAllowedOutsideWorkingHours())
                                .priority(rule.getPriority())
                                .build());
                    } catch (Exception ex) {
                        return Mono.error(ex);
                    }
                })
                .switchIfEmpty(Mono.just(EnrichmentResponse.builder()
                        .category(category)
                        .allowedOutsideWorkingHours(null)
                        .priority(null)
                        .build()
                ));
    }
}