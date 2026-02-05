package com.project2.service1.client;

import com.project2.service1.dto.EnrichmentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class EnrichmentClient {

    private final RestClient restClient;
    private final int maxAttempts;
    private final long delayMs;

    public EnrichmentClient(@Value("${app.service2.base-url}") String baseUrl,
                            @Value("${app.service2.retry.max-attempts:3}") int maxAttempts,
                            @Value("${app.service2.retry.delay-ms:500}") long delayMs) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.maxAttempts = maxAttempts;
        this.delayMs = delayMs;
    }

    public EnrichmentResponse getByCategory(String category) {
        RuntimeException lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return restClient.get()
                        .uri(uriBuilder -> uriBuilder.path("/enrichment").queryParam("category", category).build())
                        .retrieve()
                        .body(EnrichmentResponse.class);
            } catch (RuntimeException ex) {
                lastException = ex;
                if (attempt == maxAttempts) {
                    break;
                }
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Enrichment retry interrupted", interruptedException);
                }
            }
        }

        throw new IllegalStateException("Failed to fetch enrichment after retries", lastException);
    }
}