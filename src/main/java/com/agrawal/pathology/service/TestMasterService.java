package com.agrawal.pathology.service;

import com.agrawal.pathology.dto.TestDefinitionRequest;
import com.agrawal.pathology.dto.TestSectionRequest;
import com.agrawal.pathology.entity.TestDefinition;
import com.agrawal.pathology.entity.TestSection;
import com.agrawal.pathology.exception.NotFoundException;
import com.agrawal.pathology.repository.TestDefinitionRepository;
import com.agrawal.pathology.repository.TestSectionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TestMasterService {
    private final TestSectionRepository sectionRepository;
    private final TestDefinitionRepository testRepository;

    public TestMasterService(TestSectionRepository sectionRepository,
                             TestDefinitionRepository testRepository) {
        this.sectionRepository = sectionRepository;
        this.testRepository = testRepository;
    }

    public TestSection createSection(TestSectionRequest request) {
        TestSection s = new TestSection();
        s.setName(request.name());
        s.setDisplayOrder(request.displayOrder());
        s.setActive(request.active() == null ? true : request.active());
        return sectionRepository.save(s);
    }

    public List<TestSection> sections() {
        return sectionRepository.findAll();
    }

    public TestSection section(Long id) {
        return sectionRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Test section not found: " + id));
    }

    public TestDefinition createTest(TestDefinitionRequest request) {
        TestSection section = section(request.sectionId());
        TestDefinition t = new TestDefinition();
        apply(t, request, section);
        return testRepository.save(t);
    }

    public List<TestDefinition> tests() {
        return testRepository.findAll();
    }

    public TestDefinition test(Long id) {
        return testRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Test not found: " + id));
    }

    public List<TestDefinition> testsBySection(Long sectionId) {
        section(sectionId);
        return testRepository.findBySectionIdOrderByDisplayOrderAsc(sectionId);
    }

    public TestDefinition updateTest(Long id, TestDefinitionRequest request) {
        TestDefinition t = test(id);
        TestSection section = section(request.sectionId());
        apply(t, request, section);
        return testRepository.save(t);
    }

    public void deleteTest(Long id) {
        testRepository.delete(test(id));
    }

    private void apply(TestDefinition t, TestDefinitionRequest r, TestSection s) {
        t.setTestName(r.testName());
        t.setSection(s);
        t.setUnit(r.unit());
        t.setNormalRange(r.normalRange());
        t.setDisplayOrder(r.displayOrder());
        t.setResultType(r.resultType() == null ? "TEXT" : r.resultType());
        t.setActive(r.active() == null ? true : r.active());
    }
}
