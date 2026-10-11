package com.medilabo.front_service.service;

import com.medilabo.front_service.model.Assessment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AssessmentService {

    private final RestClient restClient;
    private final GatewayTokenService gatewayTokenService;

    public AssessmentService(
            @Value("${gateway.url}") String gatewayUrl,
            GatewayTokenService gatewayTokenService) {

        this.restClient = RestClient.builder()
                .baseUrl(gatewayUrl)
                .build();

        this.gatewayTokenService = gatewayTokenService;
    }

    public Assessment getPatientRisk(Long patId) {

        return restClient.get()
                .uri("/assessments/patient/{patId}/risk", patId)
                .header("Authorization", "Bearer " + gatewayTokenService.getToken())
                .retrieve()
                .body(Assessment.class);
    }
}