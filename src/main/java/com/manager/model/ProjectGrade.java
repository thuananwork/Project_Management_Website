package com.manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Table(name = "ProjectGrades")
public class ProjectGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectGradeId;

    @ManyToOne
    @JoinColumn(name = "projectId", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "lecturerId", nullable = false)
    private User lecturer;

    private BigDecimal finalGrade;

    private String comments;

    private Date gradeDate;

    // Getters and Setters
}