package com.agrawal.pathology.repository;

import com.agrawal.pathology.entity.ReportResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportResultRepository
        extends JpaRepository<ReportResult, Long> {

    List<ReportResult> findByReportId(Long reportId);

    @Query("""
        SELECT r
        FROM ReportResult r
        JOIN r.test t
        JOIN t.section s
        WHERE r.report.id = :reportId
        ORDER BY s.displayOrder ASC, t.displayOrder ASC
    """)
    List<ReportResult> findByReportIdOrderBySectionAndTest(
            @Param("reportId") Long reportId
    );

    Optional<ReportResult> findByReportIdAndTestId(
            Long reportId,
            Long testId
    );
}