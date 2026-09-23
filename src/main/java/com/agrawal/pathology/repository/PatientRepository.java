package com.agrawal.pathology.repository;

import com.agrawal.pathology.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByPatientNameContainingIgnoreCase(String patientName);
}
