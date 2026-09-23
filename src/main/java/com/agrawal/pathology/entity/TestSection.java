package com.agrawal.pathology.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "test_sections")
public class TestSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private Integer displayOrder;
    private Boolean active = true;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
