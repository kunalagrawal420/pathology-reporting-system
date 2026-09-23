package com.agrawal.pathology.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "test_definitions")
public class TestDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String testName;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private TestSection section;

    private String unit;
    private String normalRange;
    private Integer displayOrder;
    private String resultType = "TEXT";
    private Boolean active = true;

    public Long getId() { return id; }
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }
    public TestSection getSection() { return section; }
    public void setSection(TestSection section) { this.section = section; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getNormalRange() { return normalRange; }
    public void setNormalRange(String normalRange) { this.normalRange = normalRange; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    public String getResultType() { return resultType; }
    public void setResultType(String resultType) { this.resultType = resultType; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
