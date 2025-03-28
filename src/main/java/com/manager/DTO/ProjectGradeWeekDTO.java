package com.manager.DTO;

import java.math.BigDecimal;
import java.util.Date;

public class ProjectGradeWeekDTO {
    private Long id;

    private String comments;
    private Date submission_date;
    private String title;
    private Integer week_number;
    private Date start_date;
    private Date end_date;
    private BigDecimal grade;


    public ProjectGradeWeekDTO() {
    }

    public ProjectGradeWeekDTO(Long id, String comments, Date submission_date, String title, Integer week_number, Date start_date, Date end_date, BigDecimal grade) {
        this.id = id;
        this.comments = comments;
        this.submission_date = submission_date;
        this.title = title;
        this.week_number = week_number;
        this.start_date = start_date;
        this.end_date = end_date;
        this.grade = grade;
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

    public Date getSubmission_date() {
        return submission_date;
    }

    public void setSubmission_date(Date submission_date) {
        this.submission_date = submission_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getWeek_number() {
        return week_number;
    }

    public void setWeek_number(Integer week_number) {
        this.week_number = week_number;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public BigDecimal getGrade() {
        return grade;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
    }
}
