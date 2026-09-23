package com.agrawal.pathology.repository;

import com.agrawal.pathology.entity.TestSection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestSectionRepository extends JpaRepository<TestSection, Long> {
}
