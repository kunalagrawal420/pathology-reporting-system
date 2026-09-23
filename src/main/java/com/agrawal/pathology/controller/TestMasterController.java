package com.agrawal.pathology.controller;

import com.agrawal.pathology.dto.TestDefinitionRequest;
import com.agrawal.pathology.dto.TestSectionRequest;
import com.agrawal.pathology.entity.TestDefinition;
import com.agrawal.pathology.entity.TestSection;
import com.agrawal.pathology.service.TestMasterService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TestMasterController {
    private final TestMasterService service;

    public TestMasterController(TestMasterService service) { this.service = service; }

    @PostMapping("/test-sections")
    public TestSection createSection(@Valid @RequestBody TestSectionRequest request) {
        return service.createSection(request);
    }

    @GetMapping("/test-sections")
    public List<TestSection> sections() { return service.sections(); }

    @GetMapping("/test-sections/{id}")
    public TestSection section(@PathVariable Long id) { return service.section(id); }

    @PostMapping("/tests")
    public TestDefinition createTest(@Valid @RequestBody TestDefinitionRequest request) {
        return service.createTest(request);
    }

    @GetMapping("/tests")
    public List<TestDefinition> tests() { return service.tests(); }

    @GetMapping("/tests/{id}")
    public TestDefinition test(@PathVariable Long id) { return service.test(id); }

    @GetMapping("/tests/section/{sectionId}")
    public List<TestDefinition> testsBySection(@PathVariable Long sectionId) {
        return service.testsBySection(sectionId);
    }

    @PutMapping("/tests/{id}")
    public TestDefinition updateTest(@PathVariable Long id,
                                     @Valid @RequestBody TestDefinitionRequest request) {
        return service.updateTest(id, request);
    }

    @DeleteMapping("/tests/{id}")
    public void deleteTest(@PathVariable Long id) { service.deleteTest(id); }
}
