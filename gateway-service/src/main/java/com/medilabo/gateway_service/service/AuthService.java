package com.medilabo.gateway_service.service;

import com.medilabo.gateway_service.dto.LoginRequest;
import lombok.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;


/**
 * Service chargé de l'authentification et de la génération des JWT.
 *
 * Il vérifie les identifiants à l'aide du mécanisme
 * d'authentification de Spring Security.
 *
 * Après une authentification réussie, il génère un JWT signé
 * avec l'algorithme HS256 et valable pendant une heure.
 *
 * Ce jeton permet d'authentifier les requêtes adressées
 * aux routes protégées du Gateway.
 */
@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;


    /**
     * Authentifie un utilisateur et génère un JWT.
     *
     * Les identifiants sont vérifiés par AuthenticationManager.
     * Si l'authentification réussit, un jeton signé est créé
     * avec l'identité de l'utilisateur et une durée de validité
     * d'une heure.
     *
     * @param loginRequest identifiants de connexion
     * @return le JWT généré après authentification
     * @throws org.springframework.security.core.AuthenticationException
     *         si les identifiants sont invalides
     */
    public String login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("medilabo-gateway")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

}

