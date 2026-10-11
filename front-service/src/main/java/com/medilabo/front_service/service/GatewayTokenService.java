package com.medilabo.front_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GatewayTokenService {

    private final RestClient restClient;
    private final String username;
    private final String password;

    public GatewayTokenService(
            @Value("${gateway.url}") String gatewayUrl,
            @Value("${gateway.username}") String username,
            @Value("${gateway.password}") String password) {

        this.restClient = RestClient.builder()
                .baseUrl(gatewayUrl)
                .build();

        this.username = username;
        this.password = password;
    }

    public String getToken() {

        LoginRequest request = new LoginRequest(username, password);

        String token = restClient.post()
                .uri("/auth/login")
                .body(request)
                .retrieve()
                .body(String.class);

        if (token == null || token.isBlank()) {
            throw new IllegalStateException("Impossible de récupérer le JWT");
        }

        return token;
    }

    private record LoginRequest(String username, String password) {
    }
}