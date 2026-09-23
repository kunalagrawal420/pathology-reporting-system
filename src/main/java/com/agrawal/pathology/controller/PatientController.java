package com.agrawal.pathology.controller;

import com.agrawal.pathology.dto.PatientRequest;
import com.agrawal.pathology.entity.Patient;
import com.agrawal.pathology.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService service;

    public PatientController(PatientService service) { this.service = service; }

    @PostMapping
    public Patient create(@Valid @RequestBody PatientRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<Patient> findAll(@RequestParam(required = false) String query) {
        return query == null || query.isBlank() ? service.findAll() : service.search(query);
    }

    @GetMapping("/{id}")
    public Patient findById(@PathVariable Long id) { return service.findById(id); }

    @PutMapping("/{id}")
    public Patient update(@PathVariable Long id, @Valid @RequestBody PatientRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }
}
