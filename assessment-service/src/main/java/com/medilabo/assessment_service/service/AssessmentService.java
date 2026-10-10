package com.medilabo.assessment_service.service;

import com.medilabo.assessment_service.dto.NoteDTO;
import com.medilabo.assessment_service.dto.PatientDTO;
import com.medilabo.assessment_service.model.Assessment;
import com.medilabo.assessment_service.model.RiskLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.core.ParameterizedTypeReference;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class AssessmentService {

    private final RestClient patientRestClient;
    private final RestClient noteRestClient;

    private static final List<String> TRIGGER_TERMS = List.of(
            "hémoglobine a1c",
            "microalbumine",
            "taille",
            "poids",
            "fumeur",
            "fumeuse",
            "anormal",
            "cholestérol",
            "vertiges",
            "rechute",
            "réaction",
            "anticorps"
    );

    public AssessmentService(
            @Value("${patient.service.url}") String patientServiceUrl,
            @Value("${note.service.url}") String noteServiceUrl,
            @Value("${service.username}") String username,
            @Value("${service.password}") String password) {

        this.patientRestClient = RestClient.builder()
                .baseUrl(patientServiceUrl)
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();

        this.noteRestClient = RestClient.builder()
                .baseUrl(noteServiceUrl)
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();
    }

    public PatientDTO getPatient(Long patId) {
        return patientRestClient.get()
                .uri("/patients/{id}", patId)
                .retrieve()
                .body(PatientDTO.class);
    }

    public List<NoteDTO> getNotesByPatientId(Long patId) {
        return noteRestClient.get()
                .uri("/notes/patient/{patId}", patId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<NoteDTO>>() {});
    }

    private int countTriggerTerms(List<NoteDTO> notes) {

        int triggerTermsCount = 0;

        for (NoteDTO note : notes) {
            String noteText = note.getNote().toLowerCase();

            for (String term : TRIGGER_TERMS) {
                if (noteText.contains(term)) {
                    triggerTermsCount++;
                }
            }
        }
        return triggerTermsCount;
    }

    private int calculateAge(LocalDate birth) {

        return Period.between(birth, LocalDate.now()).getYears();
    }

    private String determineRiskLevel(int age, String gender, int triggerTermsCount) {

        if (age < 30) {

            if ("M".equalsIgnoreCase(gender)) {
                if (triggerTermsCount >= 5) {
                    return RiskLevel.EARLY_ONSET.name();
                }
                if (triggerTermsCount >= 3) {
                    return RiskLevel.IN_DANGER.name();
                }
            }

            if ("F".equalsIgnoreCase(gender)) {
                if (triggerTermsCount >= 7) {
                    return RiskLevel.EARLY_ONSET.name();
                }
                if (triggerTermsCount >= 4) {
                    return RiskLevel.IN_DANGER.name();
                }
            }
        }

        if (age >= 30) {
            if (triggerTermsCount >= 8) {
                return RiskLevel.EARLY_ONSET.name();
            }
            if (triggerTermsCount >= 6) {
                return RiskLevel.IN_DANGER.name();
            }
            if (triggerTermsCount >= 2) {
                return RiskLevel.BORDERLINE.name();
            }
        }

        return RiskLevel.NONE.name();
    }

    public Assessment assessPatient(Long patId) {

        PatientDTO patient = getPatient(patId);
        List<NoteDTO> notes = getNotesByPatientId(patId);

        int age = calculateAge(patient.getBirth());
        int triggerTermsCount = countTriggerTerms(notes);

        RiskLevel riskLevel = RiskLevel.valueOf(determineRiskLevel(age, patient.getGender(), triggerTermsCount));

        return new Assessment(patId, riskLevel);
    }
}