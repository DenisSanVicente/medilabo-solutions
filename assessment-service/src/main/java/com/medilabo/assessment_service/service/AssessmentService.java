package com.medilabo.assessment_service.service;

import com.medilabo.assessment_service.dto.NoteDTO;
import com.medilabo.assessment_service.dto.PatientDTO;
import com.medilabo.assessment_service.model.Assessment;
import com.medilabo.assessment_service.model.RiskLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.core.ParameterizedTypeReference;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Service métier responsable de l'évaluation du risque de diabète
 * de type 2 chez les patients.
 *
 * Il récupère les informations du patient et ses notes médicales
 * auprès des microservices correspondants, puis analyse les termes
 * déclencheurs présents dans les notes.
 *
 * Le niveau de risque est déterminé selon l'âge, le sexe
 * et le nombre de termes déclencheurs identifiés.
 *
 * Quatre niveaux de risque sont possibles :
 * NONE, BORDERLINE, IN_DANGER et EARLY_ONSET.
 */
@Service
public class AssessmentService {

    private final RestClient patientRestClient;
    private final RestClient noteRestClient;


    /**
     * Motifs de recherche correspondant aux termes déclencheurs
     * utilisés pour évaluer le risque de diabète.
     *
     * Les expressions régulières permettent de reconnaître
     * certaines variantes grammaticales des termes médicaux.
     * La recherche s'effectue sur des textes préalablement normalisés.
     */
    private static final List<String> TRIGGER_PATTERNS = List.of(
            "hemoglobine a1c",
            "microalbumine",
            "taille",
            "poids",
            "fum(?:e|es|ent|eur|euse)",
            "(?:anormal(?:e|es|s)?|anormaux)",
            "cholesterol",
            "vertiges?",
            "rechute",
            "reactions?",
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


    /**
     * Récupère les informations d'un patient depuis le microservice Patient.
     *
     * @param patId identifiant du patient
     * @return les informations du patient sous forme de DTO
     */
    public PatientDTO getPatient(Long patId) {
        return patientRestClient.get()
                .uri("/patients/{id}", patId)
                .retrieve()
                .body(PatientDTO.class);
    }


    /**
     * Récupère les notes médicales associées à un patient
     * depuis le microservice Note.
     *
     * @param patId identifiant du patient
     * @return la liste des notes médicales du patient
     */
    public List<NoteDTO> getNotesByPatientId(Long patId) {
        return noteRestClient.get()
                .uri("/notes/patient/{patId}", patId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<NoteDTO>>() {});
    }


    /**
     * Compte les occurrences des catégories de termes déclencheurs
     * présentes dans les notes médicales.
     *
     * Chaque motif est comptabilisé au maximum une fois par note,
     * même si le terme apparaît plusieurs fois dans celle-ci.
     *
     * @param notes liste des notes médicales à analyser
     * @return le nombre total de motifs détectés
     */
    private int countTriggerTerms(List<NoteDTO> notes) {

        int triggerTermsCount = 0;

        for (NoteDTO note : notes) {

            if (note.getNote() == null) {
                continue;
            }

            String noteText = normalize(note.getNote());

            for (String termPattern : TRIGGER_PATTERNS) {

                Pattern pattern = Pattern.compile(
                        "\\b(?:" + termPattern + ")\\b"
                );

                if (pattern.matcher(noteText).find()) {
                    triggerTermsCount++;
                }
            }
        }

        return triggerTermsCount;
    }


    /**
     * Normalise un texte pour faciliter la détection
     * des termes déclencheurs.
     *
     * Supprime les accents et convertit le texte en minuscules
     * afin de rendre la recherche insensible à la casse
     * et aux variations d'accentuation.
     *
     * @param text texte à normaliser
     * @return le texte normalisé
     */
    private String normalize(String text) {

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);

        return normalized
                .replaceAll("\\p{M}", "")
                .toLowerCase(java.util.Locale.ROOT);
    }


    /**
     * Calcule l'âge du patient en années révolues
     * à partir de sa date de naissance.
     *
     * @param birth date de naissance du patient
     * @return l'âge du patient en années
     */
    private int calculateAge(LocalDate birth) {

        return Period.between(birth, LocalDate.now()).getYears();
    }


    /**
     * Détermine le niveau de risque de diabète selon les règles métier.
     *
     * Pour les patients de moins de 30 ans, les seuils varient
     * selon le sexe. À partir de 30 ans, les mêmes seuils
     * sont appliqués aux hommes et aux femmes.
     *
     * @param age âge du patient
     * @param gender sexe du patient (M ou F)
     * @param triggerTermsCount nombre de termes déclencheurs détectés
     * @return le nom du niveau de risque calculé
     */
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


    /**
     * Réalise l'évaluation complète du risque de diabète d'un patient.
     *
     * Récupère les données du patient et ses notes médicales,
     * calcule son âge, compte les termes déclencheurs
     * et détermine le niveau de risque correspondant.
     *
     * @param patId identifiant du patient à évaluer
     * @return l'évaluation contenant l'identifiant du patient
     *         et son niveau de risque
     */
    public Assessment assessPatient(Long patId) {

        PatientDTO patient = getPatient(patId);
        List<NoteDTO> notes = getNotesByPatientId(patId);

        int age = calculateAge(patient.getBirth());
        int triggerTermsCount = countTriggerTerms(notes);

        RiskLevel riskLevel = RiskLevel.valueOf(determineRiskLevel(age, patient.getGender(), triggerTermsCount));

        return new Assessment(patId, riskLevel);
    }
}