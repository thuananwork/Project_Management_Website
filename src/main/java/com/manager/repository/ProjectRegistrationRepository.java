package com.manager.repository;


import com.manager.model.ProjectRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRegistrationRepository extends JpaRepository<ProjectRegistration, Long> {

    @Query(value = "SELECT project_registrations.* FROM project LEFT JOIN project_registrations ON project.project_id = project_registrations.project_id WHERE project_registrations.student_id = ?1",nativeQuery = true)
    ProjectRegistration findAllProjectByStatus(Long id);

    @Query(value = "SELECT project_registrations.* FROM project_registrations \n" +
            "INNER JOIN project ON project.project_id = project_registrations.project_id \n" +
            "INNER JOIN project_lecturers ON project.project_id = project_lecturers.project_id \n" +
            "WHERE project_lecturers.project_id = ?1",nativeQuery = true)
    ProjectRegistration findAllProjectByUserID(Long id);

    @Query(value = "SELECT project_registrations.* FROM project_registrations WHERE project_registrations.student_id = ?1",nativeQuery = true)
    ProjectRegistration findAllProjectByID(Long id);

    @Query(value = "SELECT project_registrations.* FROM project_registrations WHERE project_registrations.student_id = ?1",nativeQuery = true)
    ProjectRegistration findByProject(Long id);

    @Query(value = "SELECT COUNT(*) FROM project_registrations pr WHERE pr.student_id = ?1",nativeQuery = true)
    Integer existsByStudentID(Long studentId);

}
