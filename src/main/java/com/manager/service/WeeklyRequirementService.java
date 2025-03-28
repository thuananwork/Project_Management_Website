package com.manager.service;
import com.manager.DTO.ProjectGradeWeekDTO;
import com.manager.DTO.WeeklyProject;
import com.manager.DTO.WeeklyRequirementDTO;
import com.manager.model.Project;
import com.manager.model.WeeklyRequirement;
import com.manager.repository.ProjectRepository;
import com.manager.repository.WeeklyRequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeeklyRequirementService {
    @Autowired
    private WeeklyRequirementRepository weeklyRequirementRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public List<WeeklyRequirement> getAllWeeklyRequirements(Long id) {
        return weeklyRequirementRepository.findAllProjectByUser(id);
    }

    public List<Integer> getAvailableWeekNumbers() {
        return weeklyRequirementRepository.findDistinctWeekNumbers();
    }

    public List<WeeklyProject> getAllWeeklyProject(Long id) {
        List<Object[]> results = weeklyRequirementRepository.findAllProject(id);
        List<WeeklyProject> weeklyProjects = new ArrayList<>();

        for (Object[] result : results) {
            WeeklyProject custom = new WeeklyProject(
                    ((Number) result[0]).longValue(),
                    (String) result[1],
                    (Date) result[2],
                    (Date) result[3],
                    ((Number) result[4]).longValue(),
                    (Integer) result[7],
                    (String) result[8]
            );
            weeklyProjects.add(custom);
        }
        return weeklyProjects;
    }
    public List<ProjectGradeWeekDTO> getAllWeeklyProjectGrade(Long id) {
        List<Object[]> results = weeklyRequirementRepository.findAllProjectGrade(id);
        List<ProjectGradeWeekDTO> weeklyProjects = new ArrayList<>();

        for (Object[] result : results) {
            ProjectGradeWeekDTO custom = new ProjectGradeWeekDTO(
                    ((Number) result[0]).longValue(),
                    (String) result[1],
                    (Date) result[2],
                    (String) result[3],
                    (Integer) result[4],
                    (Date) result[5],
                    (Date) result[6],
                    (BigDecimal) result[7]
            );
            weeklyProjects.add(custom);
        }
        return weeklyProjects;
    }

    public WeeklyRequirement saveWeeklyRequirement(WeeklyRequirement weeklyRequirement) {
        return weeklyRequirementRepository.save(weeklyRequirement);
    }

    public Optional<WeeklyRequirement> getWeeklyRequirementById(Long id) {
        return weeklyRequirementRepository.findById(id);
    }

    public void deleteWeeklyRequirement(Long id) {
        weeklyRequirementRepository.deleteById(id);
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

    public void saveWeeklyReports(Long projectId, List<WeeklyRequirementDTO> weeklyRequirements) {
        for (WeeklyRequirementDTO dto : weeklyRequirements) {
            WeeklyRequirement existingRequirement = weeklyRequirementRepository
                    .findByProjectNeumber(projectId, dto.getWeekNumber())
                    .orElseThrow(() -> new IllegalArgumentException("Weekly requirement not found"));
            existingRequirement.setDescription(dto.getDescription());
            weeklyRequirementRepository.save(existingRequirement);
        }
    }
}
