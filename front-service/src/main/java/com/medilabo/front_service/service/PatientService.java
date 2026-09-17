package com.medilabo.front_service.service;

import com.medilabo.front_service.model.Patient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class PatientService {

    private final RestClient restClient;

    public PatientService(
            @Value("${gateway.url}") String gatewayUrl,
            @Value("${gateway.username}") String username,
            @Value("${gateway.password}") String password) {

        this.restClient = RestClient.builder()
                .baseUrl(gatewayUrl)
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();
    }

    public List<Patient> getPatients() {
        return restClient.get()
                .uri("/patients")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Patient>>() {});
    }

    public Patient addPatient(Patient patient) {
        return restClient.post()
                .uri("/patients")
                .body(patient)
                .retrieve()
                .body(Patient.class);
    }

    public Patient getPatientById(Long id) {
        return restClient.get()
                .uri("/patients/{id}", id)
                .retrieve()
                .body(Patient.class);
    }

    public Patient updatePatient(Long id, Patient patient) {
        return restClient.put()
                .uri("/patients/{id}", id)
                .body(patient)
                .retrieve()
                .body(Patient.class);
    }
}
