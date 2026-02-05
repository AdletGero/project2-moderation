package com.project2.service2.Controller;


import com.project2.service2.DTO.EnrichmentResponse;
import com.project2.service2.Service.EnrichmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class EnrichmentController {

    private final EnrichmentService enrichmentService;

    @GetMapping("/enrichment")
    public Mono<EnrichmentResponse> getEnrichment(@RequestParam String category) {
        return enrichmentService.getEnrichment(category);
    }
}

