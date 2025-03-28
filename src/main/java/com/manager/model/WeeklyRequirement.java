package com.manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Table(name = "WeeklyRequirements")
public class WeeklyRequirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requirementId;

    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "week_number", nullable = false)
    private Integer weekNumber;

    @Column(name = "important_dates")
    private String importantDates;

    @Column(name = "additional_requirements")
    private String additionalRequirements;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "projectId",referencedColumnName = "projectId", nullable = false)
    private Project project;

}