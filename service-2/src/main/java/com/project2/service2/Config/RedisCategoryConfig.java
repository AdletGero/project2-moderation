package com.project2.service2.config;

import com.project2.service2.DTO.EnrichmentData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisCategoryConfig {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Bean
    public ApplicationRunner seedCategories() {
        return args -> {
            Map<String, EnrichmentData> defaults = Map.of(
                    "test", EnrichmentData.builder().category("test").allowedOutsideWorkingHours(true).priority(1).build(),
                    "payment", EnrichmentData.builder().category("payment").allowedOutsideWorkingHours(false).priority(2).build(),
                    "transfer", EnrichmentData.builder().category("transfer").allowedOutsideWorkingHours(false).priority(3).build()
            );

            Flux.fromIterable(defaults.entrySet())
                    .flatMap(entry -> upsertCategory(entry.getKey(), entry.getValue()))
                    .then()
                    .block();
        };
    }

    private Mono<Boolean> upsertCategory(String category, EnrichmentData data) {
        try {
            String payload = objectMapper.writeValueAsString(data);
            String key = "category:" + category;
            return redisTemplate.opsForValue()
                    .set(key, payload)
                    .doOnSuccess(result -> log.info("Seeded enrichment rule for category={}", category));
        } catch (Exception ex) {
            return Mono.error(new IllegalStateException("Failed to serialize enrichment data for " + category, ex));
        }
    }
}