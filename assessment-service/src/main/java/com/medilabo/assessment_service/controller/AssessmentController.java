package com.medilabo.assessment_service.controller;

import com.medilabo.assessment_service.dto.NoteDTO;
import com.medilabo.assessment_service.dto.PatientDTO;
import com.medilabo.assessment_service.model.Assessment;
import com.medilabo.assessment_service.service.AssessmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;


    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @GetMapping("/patient/{patId}")
    public PatientDTO getPatient(@PathVariable Long patId) {
        return assessmentService.getPatient(patId);
    }

    @GetMapping("/patient/{patId}/notes")
    public List<NoteDTO> getNotesByPatientId(@PathVariable Long patId) {
        return assessmentService.getNotesByPatientId(patId);
    }

    @GetMapping("/patient/{patId}/risk")
    public Assessment assessPatient(@PathVariable Long patId) {
        return assessmentService.assessPatient(patId);
    }
}