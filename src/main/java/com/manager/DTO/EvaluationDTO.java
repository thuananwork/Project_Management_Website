package com.manager.DTO;

import java.math.BigDecimal;
import java.util.Date;

public class EvaluationDTO {
    private Long id;

    private String comments;
    private Date subDate;
    private String firstName;
    private String lastName;
    private String request;
    private Date startDate;
    private Date endDate;
    private String title;
    private String file;
    private Double weekly_report_grade;
    private Integer week_number;

    public EvaluationDTO() {
    }

    public EvaluationDTO(Long id, String comments, Date subDate, String firstName, String lastName, String request, Date startDate, Date endDate, String title, String file, Double weekly_report_grade, Integer week_number) {
        this.id = id;
        this.comments = comments;
        this.subDate = subDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.request = request;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.file = file;
        this.weekly_report_grade = weekly_report_grade;
        this.week_number = week_number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getSubDate() {
        return subDate;
    }

    public void setSubDate(Date subDate) {
        this.subDate = subDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Double getWeekly_report_grade() {
        return weekly_report_grade;
    }

    public void setWeekly_report_grade(Double weekly_report_grade) {
        this.weekly_report_grade = weekly_report_grade;
    }

    public Integer getWeek_number() {
        return week_number;
    }

    public void setWeek_number(Integer week_number) {
        this.week_number = week_number;
    }
}
