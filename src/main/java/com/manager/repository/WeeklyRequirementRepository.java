package com.manager.repository;


import com.manager.model.WeeklyRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyRequirementRepository extends JpaRepository<WeeklyRequirement, Long> {
    @Query(value = " SELECT weekly_requirements.*, project.title FROM weekly_requirements \n" +
            "                        INNER JOIN project ON weekly_requirements.project_id = project.project_id\n" +
            "                       INNER JOIN project_registrations ON project_registrations.project_id = project.project_id WHERE project_registrations.student_id = ?1 AND project.status = 'DangTienHanh'",nativeQuery = true)
    List<Object[]> findAllProject(Long id);

    @Query(value = " SELECT student_weekly_reports.student_report_id,student_weekly_reports.comments, student_weekly_reports.submission_date, project.title, weekly_requirements.week_number, weekly_requirements.start_date, weekly_requirements.end_date, evaluations.grade FROM weekly_requirements \n" +
            "                                   INNER JOIN project ON weekly_requirements.project_id = project.project_id\n" +
            "                                   INNER JOIN student_weekly_reports ON student_weekly_reports.requirement_id = weekly_requirements.requirement_id\n" +
            "                                   INNER JOIN evaluations ON evaluations.student_report_id = student_weekly_reports.student_report_id\n" +
            "                                  INNER JOIN project_registrations ON project_registrations.project_id = project.project_id WHERE project_registrations.student_id = ?1 AND project.status = 'DangTienHanh'",nativeQuery = true)
    List<Object[]> findAllProjectGrade(Long id);

    @Query(value = " SELECT weekly_requirements.* FROM weekly_requirements \n" +
            "                                    INNER JOIN project ON weekly_requirements.project_id = project.project_id\n" +
            "                                    INNER JOIN project_lecturers ON project_lecturers.project_id = project.project_id \n" +
            "                                   INNER JOIN project_registrations ON project_registrations.project_id = project.project_id \n" +
            "                                   WHERE project_lecturers.lecturer_id = ?1 AND project.status = 'DangTienHanh'",nativeQuery = true)
    List<WeeklyRequirement> findAllProjectByUser(Long id);

    @Query(value = " SELECT student_weekly_reports.weekly_report_grade  FROM weekly_requirements \n" +
            "INNER JOIN student_weekly_reports ON student_weekly_reports.requirement_id = weekly_requirements.requirement_id\n" +
            "                                                INNER JOIN project ON weekly_requirements.project_id = project.project_id\n" +
            "                                                INNER JOIN project_lecturers ON project_lecturers.project_id = project.project_id\n" +
            "                                               INNER JOIN project_registrations ON project_registrations.project_id = project.project_id \n" +
            "                                               WHERE project_lecturers.lecturer_id = ?1 AND project.status = 'DangTienHanh'",nativeQuery = true)
    List<Double> findAllProjectByGrade(Long id);


    @Query(value = " SELECT weekly_requirements.*  FROM weekly_requirements WHERE weekly_requirements.project_id = ?1 AND weekly_requirements.week_number = ?2",nativeQuery = true)
    Optional<WeeklyRequirement> findByProjectNeumber(Long projectId, Integer weekNumber);

    @Query(value = "SELECT DISTINCT w.week_number\n" +
            "FROM weekly_requirements w \n" +
            "INNER JOIN project\n" +
            "ORDER BY w.week_number", nativeQuery = true)
    List<Integer> findDistinctWeekNumbers();

    @Query(value = "SELECT DISTINCT w.week_number\n" +
            "FROM weekly_requirements w \n" +
            "INNER JOIN project\n" +
            "ORDER BY w.week_number", nativeQuery = true)
    List<WeeklyRequirement> findByProjectId(Long projectId);

    @Query(value = "SELECT w.* FROM weekly_requirements w WHERE w.project_id = ?1", nativeQuery = true)
    List<WeeklyRequirement> findByProject(Long projectId);

    @Modifying
    @Query("DELETE FROM WeeklyRequirement w WHERE w.project.projectId = :projectId")
    void deleteByProjectId(@Param("projectId") Long projectId);
}
