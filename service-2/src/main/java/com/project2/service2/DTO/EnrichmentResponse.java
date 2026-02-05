package com.project2.service2.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrichmentResponse {
    private String category;
    private Boolean allowedOutsideWorkingHours;
    private Integer priority;
}
