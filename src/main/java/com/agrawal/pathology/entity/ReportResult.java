package com.agrawal.pathology.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "report_results",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_report_test",
        columnNames = {"report_id", "test_id"}
    )
)
public class ReportResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private TestDefinition test;

    @Column(nullable = false)
    private String resultValue;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    public Long getId() { return id; }
    public Report getReport() { return report; }
    public void setReport(Report report) { this.report = report; }
    public TestDefinition getTest() { return test; }
    public void setTest(TestDefinition test) { this.test = test; }
    public String getResultValue() { return resultValue; }
    public void setResultValue(String resultValue) { this.resultValue = resultValue; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
