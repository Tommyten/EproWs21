package de.thbingen.epro21.model;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class CompanyKeyResult
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private Integer goalValue;

    @Column(nullable = false)
    private Integer confidenceLevel;

    @Column(nullable = false)
    private Integer achievement;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    private Date timestamp;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_objective_id")
    private  CompanyObjective companyObjective;

    @OneToMany(targetEntity = BusinessUnitObjective.class, cascade = CascadeType.ALL)
    @JoinColumn(name="company_key_result_id")
    private Set<BusinessUnitObjective> businessUnitObjectives = new HashSet<>();

    @OneToMany(targetEntity = CompanyKeyResultHistory.class, cascade = CascadeType.ALL)
    @JoinColumn(name="company_key_result_id")
    private Set<CompanyKeyResultHistory> companyKeyResultHistories = new HashSet<>();

    @OneToMany(targetEntity = BusinessUnitKeyResult.class, cascade = CascadeType.ALL)
    @JoinColumn(name="company_key_result_id")
    private Set<BusinessUnitKeyResult> businessUnitKeyResults = new HashSet<>();

    public CompanyKeyResult(Integer goalValue, Integer confidenceLevel, Integer achievement, String name, String comment, Date timestamp) {
        this.goalValue = goalValue;
        this.confidenceLevel = confidenceLevel;
        this.achievement = achievement;
        this.name = name;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public CompanyKeyResult() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getGoalValue() {
        return goalValue;
    }

    public void setGoalValue(Integer goalValue) {
        this.goalValue = goalValue;
    }

    public Integer getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(Integer confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public Integer getAchievement() {
        return achievement;
    }

    public void setAchievement(Integer achievement) {
        this.achievement = achievement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public CompanyObjective getCompanyObjective() {
        return companyObjective;
    }

    public Set<CompanyKeyResultHistory> getCompanyKeyResultHistories() {
        return companyKeyResultHistories;
    }

    public Set<BusinessUnitObjective> getBusinessUnitObjectives() {
        return businessUnitObjectives;
    }

    public Set<BusinessUnitKeyResult> getBusinessUnitKeyResults() {
        return businessUnitKeyResults;
    }
}
