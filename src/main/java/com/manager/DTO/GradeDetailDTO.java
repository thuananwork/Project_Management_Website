package com.manager.DTO;

import java.math.BigDecimal;
import java.util.Date;

public class GradeDetailDTO {
    private String title;
    private String description;
    private String feedback;
    private BigDecimal grade;
    private Date evaluation_date;
    private String firstName;
    private String lastName;

    public GradeDetailDTO() {
    }

    public GradeDetailDTO(String title, String description, String feedback, BigDecimal grade, Date evaluation_date, String firstName, String lastName) {
        this.title = title;
        this.description = description;
        this.feedback = feedback;
        this.grade = grade;
        this.evaluation_date = evaluation_date;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public BigDecimal getGrade() {
        return grade;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
    }

    public Date getEvaluation_date() {
        return evaluation_date;
    }

    public void setEvaluation_date(Date evaluation_date) {
        this.evaluation_date = evaluation_date;
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
}
