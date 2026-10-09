package com.medilabo.gateway_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class ServiceAuthConfig {

    @Value("${PATIENT_SERVICE_USERNAME}")
    private String username;

    @Value("${PATIENT_SERVICE_PASSWORD}")
    private String password;

    public String getBasicAuthHeader() {

        String credentials = username + ":" + password;

        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        return "Basic " + encodedCredentials;
    }

}