package com.manager.repository;


import com.manager.model.ProjectGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProjectGradeRepository extends JpaRepository<ProjectGrade, Long> {

    @Query(value = " SELECT DISTINCT(project.project_id), project.title, project.description, project.start_date, project.end_date, user.first_name, user.last_name\n" +
            "                        FROM project\n" +
            "                       INNER JOIN project_registrations ON project_registrations.project_id = project.project_id\n" +
            "                       INNER JOIN user ON project_registrations.student_id = user.user_id\n" +
            "                       INNER JOIN student_weekly_reports ON student_weekly_reports.student_id = user.user_id\n" +
            "                       WHERE project.status = 'DangTienHanh' ",nativeQuery = true)
    List<Object[]> findAllProject();

    @Query(value = " SELECT project.project_id, project.title, project.description, project.start_date, project.end_date, user.first_name, user.last_name, project_grades.final_grade, user.user_id\n" +
            "            FROM project\n" +
            "            INNER JOIN project_grades ON project_grades.project_id = project.project_id\n" +
            "            INNER JOIN project_registrations ON project_registrations.project_id = project.project_id\n" +
            "            INNER JOIN user ON project_registrations.student_id = user.user_id\n" +
            "            ",nativeQuery = true)
    List<Object[]> findAllGrade();

    @Query(value = " SELECT user.user_id\n" +
            "FROM project\n" +
            "INNER JOIN project_registrations ON project_registrations.project_id = project.project_id\n" +
            "INNER JOIN user ON project_registrations.student_id = user.user_id\n" +
            "WHERE project_registrations.project_id = ?1",nativeQuery = true)
    Long findAllProjectByStudentID(Long id);

    @Query(value = "SELECT project_grades.final_grade FROM project_grades WHERE project_grades.project_id = ?1",nativeQuery = true)
    Integer findStudentID(Long id);

}
