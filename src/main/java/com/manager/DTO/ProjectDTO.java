package com.manager.DTO;

import com.manager.model.Project;

import java.math.BigDecimal;
import java.util.Date;

public class ProjectDTO {
    private Long projectId;

    private String title;
    private Date startDate;
    private Date endDate;
    private String status;


    public ProjectDTO() {
    }

    public ProjectDTO(Long projectId, String title, Date startDate, Date endDate, String status) {
        this.projectId = projectId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
