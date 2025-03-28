package com.manager.DTO;

import java.time.LocalDate;

public class WeeklyRequirementDTO {

    private Long id;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer weekNumber;


    public WeeklyRequirementDTO() {
    }

    public WeeklyRequirementDTO(Long id, String description, LocalDate startDate, LocalDate endDate, Integer weekNumber) {
        this.id = id;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.weekNumber = weekNumber;
    }

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getWeekNumber() { return weekNumber; }
    public void setWeekNumber(String weekNumber) {
        this.weekNumber = Integer.parseInt(weekNumber);
    }
}
