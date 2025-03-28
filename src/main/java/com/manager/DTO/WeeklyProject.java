package com.manager.DTO;

import com.manager.model.Project;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

public class WeeklyProject {
    private Long requirementId;

    private String description;

    private Date startDate;

    private Date endDate;

    private Long projectId;

    private String title;
    private Integer weekNumber;

    public WeeklyProject() {
    }

    public WeeklyProject(Long requirementId, String description, Date startDate, Date endDate, Long projectId, Integer weekNumber, String title) {
        this.requirementId = requirementId;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectId = projectId;
        this.weekNumber = weekNumber;
        this.title = title;
    }


    public Integer getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
    }

    public Long getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(Long requirementId) {
        this.requirementId = requirementId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
