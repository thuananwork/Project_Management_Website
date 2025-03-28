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
@Table(name = "Evaluations")
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evaluationId;

    @ManyToOne
    @JoinColumn(name = "studentReportId", nullable = false)
    private StudentWeeklyReport studentReport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturerId", nullable = false)
    private User lecturer;

    private BigDecimal grade;

    private String feedback;

    private Date evaluationDate;

    // Getters and Setters
}
