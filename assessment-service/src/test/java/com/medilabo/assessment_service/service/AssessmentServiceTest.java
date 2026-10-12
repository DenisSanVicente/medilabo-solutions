package com.medilabo.assessment_service.service;

import com.medilabo.assessment_service.dto.NoteDTO;
import com.medilabo.assessment_service.dto.PatientDTO;
import com.medilabo.assessment_service.model.Assessment;
import com.medilabo.assessment_service.model.RiskLevel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class AssessmentServiceTest {

    private AssessmentService assessmentService;

    @BeforeEach
    void setUp() {

        AssessmentService realService = new AssessmentService(
                "http://localhost:8082",
                "http://localhost:8083",
                "user",
                "password"
        );

        assessmentService = Mockito.spy(realService);
    }


    // ==========================================
    // TEST 1 : AUCUN TERME DÉCLENCHEUR
    // ==========================================

    @Test
    void shouldReturnNoneWhenPatientHasNoTriggerTerms() {

        Long patId = 1L;

        PatientDTO patient = new PatientDTO();
        patient.setBirth(LocalDate.of(1980, 1, 1));
        patient.setGender("M");

        doReturn(patient)
                .when(assessmentService)
                .getPatient(patId);

        doReturn(List.of())
                .when(assessmentService)
                .getNotesByPatientId(patId);

        Assessment result = assessmentService.assessPatient(patId);

        assertEquals(RiskLevel.NONE, result.getRisk());
    }


    // ==========================================
    // TEST 2 : PATIENTS DE 30 ANS OU PLUS
    // ==========================================

    @ParameterizedTest
    @CsvSource({
            "M, 0, NONE",
            "M, 2, BORDERLINE",
            "M, 6, IN_DANGER",
            "M, 8, EARLY_ONSET",
            "F, 2, BORDERLINE",
            "F, 6, IN_DANGER",
            "F, 8, EARLY_ONSET"
    })
    void shouldDetermineRiskLevelForPatientsOver30(
            String gender,
            int triggerCount,
            RiskLevel expectedRisk) {

        Long patId = 1L;

        PatientDTO patient = new PatientDTO();
        patient.setBirth(LocalDate.of(1980, 1, 1));
        patient.setGender(gender);

        List<String> triggerTerms = List.of(
                "hémoglobine a1c",
                "microalbumine",
                "taille",
                "poids",
                "fumeur",
                "anormal",
                "cholestérol",
                "vertiges"
        );

        String noteText = String.join(
                ", ",
                triggerTerms.subList(0, triggerCount)
        );

        NoteDTO note = new NoteDTO();
        note.setNote(noteText);

        doReturn(patient)
                .when(assessmentService)
                .getPatient(patId);

        doReturn(List.of(note))
                .when(assessmentService)
                .getNotesByPatientId(patId);

        Assessment result = assessmentService.assessPatient(patId);

        assertEquals(expectedRisk, result.getRisk());
    }

    @ParameterizedTest
    @CsvSource({
            "M, 2, NONE",
            "M, 3, IN_DANGER",
            "M, 5, EARLY_ONSET",
            "F, 3, NONE",
            "F, 4, IN_DANGER",
            "F, 7, EARLY_ONSET"
    })


        // ====================================
        // TEST 3 : PATIENTS DE MOINS DE 30 ANS
        // ====================================

    void shouldDetermineRiskLevelForPatientsUnder30(
            String gender,
            int triggerCount,
            RiskLevel expectedRisk) {

        Long patId = 1L;

        PatientDTO patient = new PatientDTO();
        patient.setBirth(LocalDate.now().minusYears(25));
        patient.setGender(gender);

        List<String> triggerTerms = List.of(
                "hémoglobine a1c",
                "microalbumine",
                "taille",
                "poids",
                "fumeur",
                "anormal",
                "cholestérol"
        );

        String noteText = String.join(
                ", ",
                triggerTerms.subList(0, triggerCount)
        );

        NoteDTO note = new NoteDTO();
        note.setNote(noteText);

        doReturn(patient)
                .when(assessmentService)
                .getPatient(patId);

        doReturn(List.of(note))
                .when(assessmentService)
                .getNotesByPatientId(patId);

        Assessment result = assessmentService.assessPatient(patId);

        assertEquals(expectedRisk, result.getRisk());
    }

    @Test
    void shouldHandleCaseAccentsAndWordBoundaries() {

        Long patId = 1L;

        PatientDTO patient = new PatientDTO();
        patient.setBirth(LocalDate.of(1980, 1, 1));
        patient.setGender("M");

        NoteDTO note = new NoteDTO();
        note.setNote(
                "Le patient présente un surpoids. " +
                        "Son CHOLESTEROL est élevé. " +
                        "Il présente des VERTIGES. " +
                        "Le mot cholesterol apparaît plusieurs fois."
        );

        doReturn(patient)
                .when(assessmentService)
                .getPatient(patId);

        doReturn(List.of(note))
                .when(assessmentService)
                .getNotesByPatientId(patId);

        Assessment result = assessmentService.assessPatient(patId);

        assertEquals(RiskLevel.BORDERLINE, result.getRisk());
    }


    @ParameterizedTest
    @CsvSource({
            "anormale, réaction",
            "anormales, réaction",
            "anormal, réaction",
            "anormaux, réaction",
            "fumeur, cholestérol",
            "fumeuse, cholestérol",
            "fume, cholestérol",
            "vertige, réaction",
            "vertiges, réaction"
    })
    void shouldRecognizeTriggerTermVariations(
            String firstTerm,
            String secondTerm) {

        Long patId = 1L;

        PatientDTO patient = new PatientDTO();
        patient.setBirth(LocalDate.of(1980, 1, 1));
        patient.setGender("M");

        NoteDTO note = new NoteDTO();
        note.setNote(firstTerm + " " + secondTerm);

        doReturn(patient)
                .when(assessmentService)
                .getPatient(patId);

        doReturn(List.of(note))
                .when(assessmentService)
                .getNotesByPatientId(patId);

        Assessment result = assessmentService.assessPatient(patId);

        assertEquals(RiskLevel.BORDERLINE, result.getRisk());
    }

    @ParameterizedTest
    @CsvSource({
            "1, 1966-12-31, F, NONE",
            "2, 1945-06-24, M, BORDERLINE",
            "3, 2004-06-18, M, IN_DANGER",
            "4, 2002-06-28, F, EARLY_ONSET"
    })
    void shouldReturnExpectedRiskForOfficialPatients(
            Long patId,
            LocalDate birth,
            String gender,
            RiskLevel expectedRisk) {

        PatientDTO patient = new PatientDTO();
        patient.setBirth(birth);
        patient.setGender(gender);

        List<NoteDTO> notes = switch (patId.intValue()) {

            case 1 -> List.of(
                    createNote("Le patient déclare qu'il se sent très bien. " +
                            "Poids égal ou inférieur au poids recommandé.")
            );

            case 2 -> List.of(
                    createNote("Le patient déclare qu'il ressent beaucoup de stress au travail. " +
                            "Il se plaint également que son audition est anormale dernièrement."),
                    createNote("Le patient déclare avoir fait une réaction aux médicaments. " +
                            "Il remarque également que son audition continue d'être anormale.")
            );

            case 3 -> List.of(
                    createNote("Le patient déclare qu'il fume depuis peu."),
                    createNote("Le patient déclare qu'il est fumeur. " +
                            "Il se plaint également de crises d'apnée respiratoire anormales. " +
                            "Tests de laboratoire indiquant un taux de cholestérol LDL élevé.")
            );

            case 4 -> List.of(
                    createNote("Le patient déclare qu'il lui est devenu difficile de monter les escaliers. " +
                            "Tests de laboratoire indiquant que les anticorps sont élevés. " +
                            "Réaction aux médicaments."),
                    createNote("Le patient déclare qu'il a mal au dos."),
                    createNote("Le patient déclare avoir commencé à fumer depuis peu. " +
                            "Hémoglobine A1C supérieure au niveau recommandé."),
                    createNote("Taille, Poids, Cholestérol, Vertige et Réaction.")
            );

            default -> throw new IllegalArgumentException("Patient inconnu");
        };

        doReturn(patient)
                .when(assessmentService)
                .getPatient(patId);

        doReturn(notes)
                .when(assessmentService)
                .getNotesByPatientId(patId);

        Assessment result = assessmentService.assessPatient(patId);

        assertEquals(expectedRisk, result.getRisk());
    }

    private NoteDTO createNote(String text) {

        NoteDTO note = new NoteDTO();
        note.setNote(text);

        return note;
    }

    @Test
    void shouldNotCountTriggerTermsInsideOtherWords() {

        Long patId = 1L;

        PatientDTO patient = new PatientDTO();
        patient.setBirth(LocalDate.of(1980, 1, 1));
        patient.setGender("M");

        NoteDTO note = createNote(
                "Le patient présente un surpoids et une réactionnaire attitude."
        );

        doReturn(patient)
                .when(assessmentService)
                .getPatient(patId);

        doReturn(List.of(note))
                .when(assessmentService)
                .getNotesByPatientId(patId);

        Assessment result = assessmentService.assessPatient(patId);

        assertEquals(RiskLevel.NONE, result.getRisk());
    }
}