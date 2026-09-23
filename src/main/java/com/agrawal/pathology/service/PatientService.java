package com.agrawal.pathology.service;

import com.agrawal.pathology.dto.PatientRequest;
import com.agrawal.pathology.entity.Patient;
import com.agrawal.pathology.exception.NotFoundException;
import com.agrawal.pathology.repository.PatientRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PatientService {
    private final PatientRepository repository;

    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

    public Patient create(PatientRequest request) {
        Patient p = new Patient();
        apply(p, request);
        return repository.save(p);
    }

    public List<Patient> findAll() {
        return repository.findAll();
    }

    public List<Patient> search(String query) {
        return repository.findByPatientNameContainingIgnoreCase(query);
    }

    public Patient findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Patient not found: " + id));
    }

    public Patient update(Long id, PatientRequest request) {
        Patient p = findById(id);
        apply(p, request);
        return repository.save(p);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private void apply(Patient p, PatientRequest request) {
        p.setPatientName(request.patientName());
        p.setSex(request.sex());
        p.setDateOfBirth(request.dateOfBirth());
        p.setPhone(request.phone());
    }
}
