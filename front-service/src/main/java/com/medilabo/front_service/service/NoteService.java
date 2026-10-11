package com.medilabo.front_service.service;


import com.medilabo.front_service.model.Note;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class NoteService {

    private final RestClient restClient;
    private final GatewayTokenService gatewayTokenService;

    public NoteService(
            @Value("${gateway.url}") String gatewayUrl,
            GatewayTokenService gatewayTokenService) {

        this.restClient = RestClient.builder()
                .baseUrl(gatewayUrl)
                .build();

        this.gatewayTokenService = gatewayTokenService;
    }


    public List<Note> getNotesByPatientId(Long patId) {
        return restClient.get()
                .uri("/notes/patient/{patId}", patId)
                .header("Authorization", "Bearer " + gatewayTokenService.getToken())
                .retrieve()
                .body(new ParameterizedTypeReference<List<Note>>() {});
    }

    public Note addNote(Note note) {
        return restClient.post()
                .uri("/notes")
                .body(note)
                .header("Authorization", "Bearer " + gatewayTokenService.getToken())
                .retrieve()
                .body(Note.class);
    }

    public Note updateNote(String id, Note note) {
        return restClient.put()
                .uri("/notes/{id}", id)
                .body(note)
                .header("Authorization", "Bearer " + gatewayTokenService.getToken())
                .retrieve()
                .body(Note.class);
    }
}
