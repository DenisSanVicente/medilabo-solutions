package com.medilabo.front_service.controller;

import com.medilabo.front_service.model.Patient;
import com.medilabo.front_service.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patients")
    public String getPatients(Model model) {
        model.addAttribute("patients", patientService.getPatients());
        return "patients";
    }

    @GetMapping("/patients/add")
    public String showAddPatientForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "patient-add";
    }

    @PostMapping("/patients/add")
    public String addPatient(
            @Valid @ModelAttribute Patient patient,
            BindingResult result) {

        if (result.hasErrors()) {
            return "patient-add";
        }

        patientService.addPatient(patient);

        return "redirect:/patients";
    }

    @GetMapping("/patients/edit/{id}")
    public String showUpdatePatientForm(@PathVariable Long id, Model model) {

        Patient patient = patientService.getPatientById(id);

        model.addAttribute("patient", patient);

        return "patient-edit";
    }

    @PostMapping("/patients/edit/{id}")
    public String updatePatient(
            @PathVariable Long id,
            @Valid @ModelAttribute Patient patient,
            BindingResult result) {

        if (result.hasErrors()) {
            return "patient-edit";
        }

        patientService.updatePatient(id, patient);

        return "redirect:/patients";
    }
}
