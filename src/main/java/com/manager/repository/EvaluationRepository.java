package com.manager.repository;


import com.manager.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    @Query(value = " SELECT student_weekly_reports.student_report_id,student_weekly_reports.comments, student_weekly_reports.submission_date, user.first_name, user.last_name, weekly_requirements.description, weekly_requirements.start_date, weekly_requirements.end_date, project.title, student_weekly_reports.report_file_path, student_weekly_reports.weekly_report_grade,weekly_requirements.week_number\n" +
            "                       FROM student_weekly_reports\n" +
            "                       INNER JOIN user ON student_weekly_reports.student_id = user.user_id\n" +
            "                      INNER JOIN weekly_requirements ON weekly_requirements.requirement_id = student_weekly_reports.requirement_id\n" +
            "                        INNER JOIN project ON project.project_id = weekly_requirements.project_id\n" +
            "                        WHERE project.status = 'DangTienHanh'",nativeQuery = true)
    List<Object[]> findAllProject();

    @Query(value = "SELECT student_weekly_reports.student_report_id, student_weekly_reports.comments, student_weekly_reports.submission_date, user.first_name,user.last_name, project.description, weekly_requirements.start_date,weekly_requirements.end_date,project.title, student_weekly_reports.report_file_path, student_weekly_reports.weekly_report_grade, weekly_requirements.week_number\n" +
            "FROM weekly_requirements \n" +
            "                                                INNER JOIN project ON weekly_requirements.project_id = project.project_id\n" +
            "                                               INNER JOIN project_lecturers ON project_lecturers.project_id = project.project_id \n" +
            "                                               INNER JOIN project_registrations ON project_registrations.project_id = project.project_id\n" +
            "                                               INNER JOIN student_weekly_reports ON student_weekly_reports.requirement_id = weekly_requirements.requirement_id\n" +
            "                                               INNER JOIN user ON user.user_id = student_weekly_reports.student_id\n" +
            "                                               WHERE project_lecturers.lecturer_id = ?1 AND project.status = 'DangTienHanh'",nativeQuery = true)
    List<Object[]> findAllProjects(Long id);

    @Query(value = "SELECT DISTINCT(weekly_requirements.week_number)\n" +
            "FROM weekly_requirements \n" +
            "                                                INNER JOIN project ON weekly_requirements.project_id = project.project_id\n" +
            "                                               INNER JOIN project_lecturers ON project_lecturers.project_id = project.project_id \n" +
            "                                               INNER JOIN project_registrations ON project_registrations.project_id = project.project_id\n" +
            "                                               INNER JOIN student_weekly_reports ON student_weekly_reports.requirement_id = weekly_requirements.requirement_id\n" +
            "                                               INNER JOIN user ON user.user_id = student_weekly_reports.student_id\n" +
            "                                               WHERE project_lecturers.lecturer_id = ?1 AND project.status = 'DangTienHanh'",nativeQuery = true)
    List<Integer> findAllProjectsWeek(Long id);

    @Query(value = "SELECT project.title, project.description, project.start_date, project.end_date, project_grades.comments, project_grades.final_grade, user.first_name, user.last_name\n" +
            "FROM project_grades \n" +
            "INNER JOIN user ON project_grades.lecturer_id = user.user_id\n" +
            "INNER JOIN project ON project_grades.project_id = project.project_id\n" +
            "WHERE project_grades.student_id = ?1",nativeQuery = true)
    List<Object[]> findAllEvaluation(Long id);

    @Query(value = "SELECT project.title, project.description, e.feedback, e.grade, e.evaluation_date, user.first_name, user.last_name\n" +
            "FROM evaluations e \n" +
            "INNER JOIN student_weekly_reports ON student_weekly_reports.student_report_id = e.student_report_id\n" +
            "INNER JOIN weekly_requirements ON weekly_requirements.requirement_id = student_weekly_reports.requirement_id\n" +
            "INNER JOIN project ON weekly_requirements.project_id = project.project_id\n" +
            "INNER JOIN user ON user.user_id = student_weekly_reports.student_id\n" +
            "WHERE weekly_requirements.project_id = ?1 AND user.user_id = ?2", nativeQuery = true)
    List<Object[]> findByProjectAndStudent(Long projectId, Long studentId);

}
