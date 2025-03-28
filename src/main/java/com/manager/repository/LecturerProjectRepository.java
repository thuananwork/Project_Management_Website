package com.manager.repository;

import com.manager.model.ProjectLecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LecturerProjectRepository extends JpaRepository<ProjectLecturer, Long> {
    @Query(value = " SELECT project_lecturers.assignment_id, user.first_name, user.last_name, project.title, project.start_date, project.end_date FROM project_lecturers \n" +
            "            INNER JOIN user ON project_lecturers.lecturer_id = user.user_id\n" +
            "            INNER JOIN project ON project_lecturers.project_id = project.project_id",nativeQuery = true)
    List<Object[]> findAllProject();
}
