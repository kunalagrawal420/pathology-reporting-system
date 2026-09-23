package com.agrawal.pathology.repository;

import com.agrawal.pathology.entity.TestDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TestDefinitionRepository extends JpaRepository<TestDefinition, Long> {
    List<TestDefinition> findBySectionIdOrderByDisplayOrderAsc(Long sectionId);
}
