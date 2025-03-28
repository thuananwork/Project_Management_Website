package com.manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Table(name = "StudentWeeklyReports")
public class StudentWeeklyReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentReportId;

    @ManyToOne
    @JoinColumn(name = "requirementId", nullable = false)
    private WeeklyRequirement requirement;

    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    private User student;

    private Date submissionDate;

    @Column(nullable = true)
    private Double weeklyReportGrade;

    private String reportFilePath;

    private String comments;

    // Getters and Setters
}