package com.manager.repository;

import com.manager.DTO.ProjectDTO;
import com.manager.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query(value = "SELECT * FROM project where project_id = ?1",nativeQuery = true)
    Project findProjectById(Long id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM weekly_requirements WHERE project_id = :projectId", nativeQuery = true)
    void deleteWeeklyRequirementsByProjectId(@Param("projectId") Long projectId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM project WHERE project_id = :projectId", nativeQuery = true)
    void deleteProjectById(@Param("projectId") Long projectId);

    @Query(value = "SELECT * FROM project WHERE status = N'Đang tiến hành' or status = N'Hủy';",nativeQuery = true)
    List<Project> findAllByStatus();

    @Query(value = "SELECT p.* \n" +
            "                   FROM project p \n" +
            "                   LEFT JOIN project_registrations pr ON p.project_id = pr.project_id\n" +
            "                   WHERE pr.project_id IS NULL",nativeQuery = true)
    List<Project> findAllByStatusBy();

    @Query(value = "SELECT p.* FROM project p WHERE p.status = ?1",nativeQuery = true)
    List<Project> findAllByStatuss(String status);

    @Query(value = "SELECT p.* FROM project p WHERE p.status = ?1 AND p.title LIKE CONCAT('%', ?2, '%')",nativeQuery = true)
    List<Project> findAllBylikeStatuss(String status, String keyword);

    @Query(value = "SELECT p.* FROM project p WHERE p.title LIKE CONCAT('%', ?1, '%');",nativeQuery = true)
    List<Project> findAllBylikeKey(String status);

    @Query(value = "SELECT * FROM project WHERE status = N'Đang tiến hành' or status = N'Hủy';",nativeQuery = true)
    List<Project> findAllPByStatus();

    @Query(value = "SELECT project.* FROM project LEFT JOIN project_registrations ON project.project_id = project_registrations.project_id WHERE project_registrations.student_id = ?1",nativeQuery = true)
    Project findAllProjectByStatus(Long id);

    @Query(value = "SELECT p.* FROM project_lecturers pl INNER JOIN user u ON pl.lecturer_id = u.user_id \n" +
            "INNER JOIN project p ON pl.project_id = p.project_id  \n" +
            "INNER JOIN project_registrations ON project_registrations.project_id = p.project_id\n" +
            "WHERE pl.lecturer_id = ?1 AND p.status LIKE '%DangTienHanh%'",nativeQuery = true)
    List<Project> findAllProject(Long id);

    @Query(value = "SELECT \n" +
            "    p.project_id, \n" +
            "    p.title, \n" +
            "    p.description, \n" +
            "    p.start_date, \n" +
            "    p.end_date, \n" +
            "    s.first_name,\n" +
            "    s.last_name\n" +
            "FROM project_lecturers pl\n" +
            "INNER JOIN user u ON pl.lecturer_id = u.user_id\n" +
            "INNER JOIN project p ON pl.project_id = p.project_id\n" +
            "INNER JOIN project_registrations pr ON pr.project_id = p.project_id\n" +
            "INNER JOIN user s ON pr.student_id = s.user_id \n" +
            "WHERE pl.lecturer_id = ?1 \n" +
            "  AND pr.status LIKE '%ChoXacNhan%';\n",nativeQuery = true)
    List<Object[]> findAllProjectConfirm(Long id);


    @Query(value = "SELECT COUNT(pl.assignment_id) AS total_projects_approved\n" +
            "FROM project_lecturers pl\n" +
            "INNER JOIN user u ON pl.lecturer_id = u.user_id\n" +
            "INNER JOIN project p ON pl.project_id = p.project_id\n" +
            "INNER JOIN project_registrations pr ON pr.project_id = p.project_id\n" +
            "WHERE pl.lecturer_id = ?1",nativeQuery = true)
    Integer findAllProjectConfirms(Long id);

    @Query(value = "SELECT COUNT(pl.assignment_id) AS total_projects_approved\n" +
            "FROM project_lecturers pl\n" +
            "INNER JOIN user u ON pl.lecturer_id = u.user_id\n" +
            "INNER JOIN project p ON pl.project_id = p.project_id\n" +
            "INNER JOIN project_registrations pr ON pr.project_id = p.project_id\n" +
            "WHERE pl.lecturer_id = ?1\n" +
            "  AND pr.status = 'ChoXacNhan';",nativeQuery = true)
    Integer findAllProjectConfirmss(Long id);

    @Query(value = "SELECT project.project_id, project.title, project.start_date, project.end_date, project.status FROM project WHERE (project.start_date BETWEEN :startDate AND :endDate) OR (project.end_date BETWEEN :startDate AND :endDate)", nativeQuery = true)
    List<Object[]> findAllByStartDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT project.project_id, project.title, project.start_date, project.end_date, project.status FROM project ", nativeQuery = true)
    List<Object[]> findAllProject();

    @Query("SELECT COUNT(p) FROM Project p")
    Long countAllProjects();

    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = 'HoanThanh'")
    Long countCompletedProjects();

    @Query("SELECT COUNT(p) FROM Project p WHERE p.status != 'HoanThanh'")
    Long countIncompleteProjects();
}
