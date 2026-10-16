package com.medilabo.patient_service.service;

import com.medilabo.patient_service.exception.PatientNotFoundException;
import com.medilabo.patient_service.model.Patient;
import com.medilabo.patient_service.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service métier chargé de la gestion des patients.
 *
 * Il assure les opérations de consultation, de création
 * et de modification des données des patients en s'appuyant
 * sur PatientRepository pour les accès à la base de données.
 *
 * Une exception PatientNotFoundException est levée lorsqu'un
 * patient recherché ou à modifier n'existe pas.
 */
@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient findPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(
                        "Le patient " + id + " n'existe pas dans la base de données"
                ));
    }

    public Patient addPatient(Patient patient) {
        return patientRepository.save(patient);
    }


    /**
     * Met à jour les informations d'un patient existant.
     *
     * Recherche le patient à partir de son identifiant, puis
     * remplace ses informations par celles fournies avant
     * d'enregistrer les modifications en base de données.
     *
     * @param id identifiant du patient à modifier
     * @param patient nouvelles informations du patient
     * @return le patient après enregistrement des modifications
     * @throws PatientNotFoundException si le patient n'existe pas
     */

    public Patient updatePatient(Long id, Patient patient) {

        Patient patientToUpdate = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(
                        "Le patient " + id + " n'existe pas dans la base de données"
                ));

        patientToUpdate.setFirstName(patient.getFirstName());
        patientToUpdate.setLastName(patient.getLastName());
        patientToUpdate.setBirth(patient.getBirth());
        patientToUpdate.setGender(patient.getGender());
        patientToUpdate.setAddress(patient.getAddress());
        patientToUpdate.setPhone(patient.getPhone());

        return patientRepository.save(patientToUpdate);
    }
}

