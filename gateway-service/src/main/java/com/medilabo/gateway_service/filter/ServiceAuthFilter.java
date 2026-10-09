package com.medilabo.gateway_service.filter;

import com.medilabo.gateway_service.config.ServiceAuthConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Component
@RequiredArgsConstructor
public class ServiceAuthFilter {

    private final ServiceAuthConfig serviceAuthConfig;

    public HandlerFilterFunction<ServerResponse, ServerResponse> basicAuth() {

        return (request, next) -> {

            ServerRequest modifiedRequest = ServerRequest.from(request)
                    .headers(headers -> headers.set(
                            HttpHeaders.AUTHORIZATION,
                            serviceAuthConfig.getBasicAuthHeader()
                    ))
                    .build();

            return next.handle(modifiedRequest);
        };
    }

}