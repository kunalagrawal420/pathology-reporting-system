package com.agrawal.pathology.repository;

import com.agrawal.pathology.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByReferenceNo(String referenceNo);
    List<Report> findByPatientIdOrderByReportDateDesc(Long patientId);
}
