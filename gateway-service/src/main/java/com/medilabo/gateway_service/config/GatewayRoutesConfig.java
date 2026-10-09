package com.medilabo.gateway_service.config;

import com.medilabo.gateway_service.filter.ServiceAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouterFunction<ServerResponse> patientRoute(
            ServiceAuthFilter serviceAuthFilter,
            @Value("${PATIENT_SERVICE_URL:http://localhost:8082}") String patientServiceUrl) {

        return route("patient-service")
                .route(path("/patients/**"), http())
                .before(uri(patientServiceUrl))
                .filter(serviceAuthFilter.basicAuth())
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> noteRoute(
            ServiceAuthFilter serviceAuthFilter,
            @Value("${NOTE_SERVICE_URL:http://localhost:8083}") String noteServiceUrl) {

        return route("note-service")
                .route(path("/notes/**"), http())
                .before(uri(noteServiceUrl))
                .filter(serviceAuthFilter.basicAuth())
                .build();
    }
}