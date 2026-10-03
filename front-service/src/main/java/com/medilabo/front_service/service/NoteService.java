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

    public NoteService(
            @Value("${gateway.url}") String gatewayUrl,
            @Value("${gateway.username}") String username,
            @Value("${gateway.password}") String password) {

        this.restClient = RestClient.builder()
                .baseUrl(gatewayUrl)
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();
    }


    public List<Note> getNotesByPatientId(Long patId) {
        return restClient.get()
                .uri("/notes/patient/{patId}", patId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Note>>() {});
    }

    public Note addNote(Note note) {
        return restClient.post()
                .uri("/notes")
                .body(note)
                .retrieve()
                .body(Note.class);
    }

    public Note updateNote(String id, Note note) {
        return restClient.put()
                .uri("/notes/{id}", id)
                .body(note)
                .retrieve()
                .body(Note.class);
    }
}
