package com.medilabo.front_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


/**
 * Service chargé de l'authentification technique du frontend
 * auprès du Gateway.
 *
 * Il utilise les identifiants configurés dans l'application
 * pour demander un JWT via le point d'entrée /auth/login.
 *
 * Le jeton obtenu permet au frontend d'effectuer des requêtes
 * authentifiées vers les API exposées par le Gateway.
 */
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


    /**
     * Demande un JWT au Gateway à partir des identifiants configurés.
     *
     * Envoie une requête HTTP POST vers /auth/login et récupère
     * le jeton retourné après authentification.
     *
     * @return le JWT permettant d'authentifier les appels aux API
     * @throws IllegalStateException si la réponse ne contient aucun jeton
     */
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