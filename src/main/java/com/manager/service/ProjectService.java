package com.manager.service;

import com.manager.DTO.ProjectConfirm;
import com.manager.DTO.ProjectDTO;
import com.manager.DTO.WeeklyProject;
import com.manager.model.Project;
import com.manager.model.ProjectRegistration;
import com.manager.model.User;
import com.manager.model.WeeklyRequirement;
import com.manager.repository.ProjectRegistrationRepository;
import com.manager.repository.ProjectRepository;
import com.manager.repository.UserRepository;
import com.manager.repository.WeeklyRequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRegistrationRepository projectRegistrationRepository;
    @Autowired
    private WeeklyRequirementRepository weeklyRequirementRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAllByStatus();
    }

    public List<Project> getAllProjectsBy() {
        return projectRepository.findAllByStatusBy();
    }

    public Project getAllProjectsByStatus(Long id) {
        return projectRepository.findAllProjectByStatus(id);
    }

    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public Project findProjectById(Long id){
        Project project = projectRepository.findProjectById(id);
        return project;
    }

    public void registerProject(Long projectId, Long studentId) {
        Optional<Project> project = projectRepository.findById(projectId);
        Optional<User> student = userRepository.findById(studentId);

        if (project.isPresent() && student.isPresent()) {
            ProjectRegistration registration = new ProjectRegistration();
            registration.setProject(project.get());
            registration.setStudent(student.get());
            registration.setStatus(ProjectRegistration.Status.ChoXacNhan);
            projectRegistrationRepository.save(registration);
        }
    }

    public List<ProjectConfirm> getAllProjectsConfirm(Long id) {
        List<Object[]> results = projectRepository.findAllProjectConfirm(id);
        List<ProjectConfirm> projectConfirms = new ArrayList<>();

        for (Object[] result : results) {
            ProjectConfirm custom = new ProjectConfirm(
                    ((Number) result[0]).longValue(),
                    (String) result[1],
                    (String) result[2],
                    (Date) result[3],
                    (Date) result[4],
                    (String) result[5],
                    (String) result[6]
            );
            projectConfirms.add(custom);
        }
        return projectConfirms;
    }

    public List<Map<String, String>> calculateWeeks(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đồ án"));

        List<Map<String, String>> weeks = new ArrayList<>();

        LocalDate startDate = project.getStartDate();
        LocalDate endDate = project.getEndDate();
        int weekNumber = 1;

        while (!startDate.isAfter(endDate)) {
            LocalDate weekEnd = startDate.plusDays(6).isAfter(endDate) ? endDate : startDate.plusDays(6);

            Map<String, String> week = new HashMap<>();
            week.put("startDate", startDate.toString());
            week.put("endDate", weekEnd.toString());
            week.put("weekNumber", String.valueOf(weekNumber));

            weeks.add(week);
            startDate = weekEnd.plusDays(1);
            weekNumber++;
        }

        return weeks;
    }

    public List<ProjectDTO> getAllProjectsConfirm() {
        List<Object[]> results = projectRepository.findAllProject();
        List<ProjectDTO> projectConfirms = new ArrayList<>();

        for (Object[] result : results) {
            ProjectDTO custom = new ProjectDTO(
                    ((Number) result[0]).longValue(),
                    (String) result[1],
                    (Date) result[2],
                    (Date) result[3],
                    (String) result[4]
            );
            projectConfirms.add(custom);
        }
        return projectConfirms;
    }

    public List<ProjectDTO> getAllProjects(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate) {
        List<Object[]> results = projectRepository.findAllByStartDateBetween(startDate, endDate);
        List<ProjectDTO> projectConfirms = new ArrayList<>();

        for (Object[] result : results) {
            ProjectDTO custom = new ProjectDTO(
                    ((Number) result[0]).longValue(),
                    (String) result[1],
                    (Date) result[2],
                    (Date) result[3],
                    (String) result[4]
            );
            projectConfirms.add(custom);
        }
        return projectConfirms;
    }
}
