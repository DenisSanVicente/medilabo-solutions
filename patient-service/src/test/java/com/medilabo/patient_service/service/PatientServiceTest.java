package com.medilabo.patient_service.service;

import com.medilabo.patient_service.exception.PatientNotFoundException;
import com.medilabo.patient_service.model.Patient;
import com.medilabo.patient_service.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    private Patient patient;

    @Mock
    PatientRepository patientRepository;

    @InjectMocks
    PatientService patientService;

    @Test
    void getAllPatients_shouldReturnAllPatients() {

        // ARRANGE
        Patient patient1 = new Patient();
        patient1.setLastName("Test1");

        Patient patient2 = new Patient();
        patient2.setLastName("Test2");

        when(patientRepository.findAll()).thenReturn(List.of(patient1, patient2));

        // ACT
        List<Patient> patients = patientService.getAllPatients();

        // ASSERT
        assertEquals(2, patients.size());
        verify(patientRepository).findAll();
    }

    @Test
    void findPatientById_shouldReturnAPatientById() {

        // ARRANGE
        Patient patient1 = new Patient();
        patient1.setId(1L);

        when(patientRepository.findById(patient1.getId())).thenReturn(Optional.of(patient1));

        // ACT
        Patient result = patientService.findPatientById(patient1.getId());

        // ASSERT
        assertEquals(patient1, result);
        verify(patientRepository).findById(patient1.getId());
    }

    @Test
    void findPatientById_shouldThrowsExceptionWhenPatientDoesNotExist() {

        // ARRANGE
        Long incorrectId = 99L;

        when(patientRepository.findById(incorrectId)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(
                PatientNotFoundException.class,
                () -> patientService.findPatientById(incorrectId)
        );

        verify(patientRepository).findById(incorrectId);
    }

    @Test
    void addPatient_shouldAddPatient() {

        // ARRANGE
        Patient patient1 = new Patient();
        patient1.setId(1L);

        when(patientRepository.save(patient1)).thenReturn(patient1);

        // ACT
        Patient result = patientService.addPatient(patient1);

        // ASSERT
        assertEquals(1L, result.getId());
        verify(patientRepository).save(patient1);

    }

    @Test
    void updatePatient_shouldReturnUpdatedPatient() {

        // ARRANGE
        Patient existingPatient = new Patient();
        existingPatient.setId(1L);
        existingPatient.setLastName("Existing");

        Patient updatedPatient = new Patient();
        updatedPatient.setLastName("Updated");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.save(existingPatient)).thenReturn(existingPatient);


        // ACT
        Patient result = patientService.updatePatient(1L, updatedPatient);

        // ASSERT
        assertEquals("Updated", result.getLastName());
        verify(patientRepository).findById(1L);
        verify(patientRepository).save(existingPatient);
    }

    @Test
    void updatedPatient_shouldThrowsExceptionWhenPatientDoesNotExist() {

        // ARRANGE
        Patient updatedPatient = new Patient();
        updatedPatient.setId(99L);

        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(
                PatientNotFoundException.class,
                () -> patientService.updatePatient(99L, updatedPatient));

        verify(patientRepository).findById(99L);
        verify(patientRepository, never()).save(any(Patient.class));

    }




}
