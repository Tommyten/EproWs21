package de.thbingen.epro.model.dto;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.OffsetDateTime;

@Relation(collectionRelation = "businessUnitKeyResults", itemRelation = "businessUnitKeyResult")
public class BusinessUnitKeyResultDto extends RepresentationModel<BusinessUnitKeyResultDto> {

    @NotBlank
    private String name;
    private Float currentValue;
    @Min(value = 1, message = "Goal Value must be at least 1")
    private Float goalValue;
    @Min(value = 0, message = "You shouldn't be negatively confident")
    @Max(value = 100, message = "Don't be too overconfident")
    private Float confidenceLevel;
    @Min(value = 0, message = "Achievement must be 0 when creating a new Bussines Unit KeyResult")
    @Max(value = 0, message = "Achievement must be 0 when creating a new Bussines Unit KeyResult")
    private Float achievement = 0f;
    @NotBlank
    private String comment;
    private final OffsetDateTime timestamp = OffsetDateTime.now();


    public BusinessUnitKeyResultDto() {
    }

    public BusinessUnitKeyResultDto(String name, float currentValue, float goalValue, float confidenceLevel, float achievement, String comment) {
        this.name = name;
        this.currentValue = currentValue;
        this.goalValue = goalValue;
        this.confidenceLevel = confidenceLevel;
        this.achievement = achievement;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Float currentValue) {
        this.currentValue = currentValue;
    }

    public Float getGoalValue() {
        return goalValue;
    }

    public void setGoalValue(Float goalValue) {
        this.goalValue = goalValue;
    }

    public Float getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(Float confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public Float getAchievement() {
        return achievement;
    }

    public void setAchievement(Float achievement) {
        this.achievement = achievement;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
}
