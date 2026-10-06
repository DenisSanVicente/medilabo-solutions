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

    private final RestClient restClient;

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
            @Value("${gateway.url}") String gatewayUrl,
            @Value("${gateway.username}") String username,
            @Value("${gateway.password}") String password) {

        this.restClient = RestClient.builder()
                .baseUrl(gatewayUrl)
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();
    }

    public PatientDTO getPatient(Long patId) {
        return restClient.get()
                .uri("/patients/{id}", patId)
                .retrieve()
                .body(PatientDTO.class);
    }

    public List<NoteDTO> getNotesByPatientId(Long patId) {
        return restClient.get()
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

    private RiskLevel determineRiskLevel(int age, String gender, int triggerTermsCount) {

        if (age < 30) {

            if ("M".equalsIgnoreCase(gender)) {
                if (triggerTermsCount >= 5) {
                    return RiskLevel.EARLY_ONSET;
                }
                if (triggerTermsCount >= 3) {
                    return RiskLevel.IN_DANGER;
                }
            }

            if ("F".equalsIgnoreCase(gender)) {
                if (triggerTermsCount >= 7) {
                    return RiskLevel.EARLY_ONSET;
                }
                if (triggerTermsCount >= 4) {
                    return RiskLevel.IN_DANGER;
                }
            }
        }

        if (age > 30) {
            if (triggerTermsCount >= 8) {
                return RiskLevel.EARLY_ONSET;
            }
            if (triggerTermsCount >= 6) {
                return RiskLevel.IN_DANGER;
            }
            if (triggerTermsCount >= 2) {
                return RiskLevel.BORDERLINE;
            }
        }

        return RiskLevel.NONE;
    }

    public Assessment assessPatient(Long patId) {

        PatientDTO patient = getPatient(patId);
        List<NoteDTO> notes = getNotesByPatientId(patId);
        int age = calculateAge(patient.getBirth());
        int triggerTermsCount = countTriggerTerms(notes);
        RiskLevel riskLevel = determineRiskLevel(age, patient.getGender(), triggerTermsCount);

        return new Assessment(patId, riskLevel);
    }
}