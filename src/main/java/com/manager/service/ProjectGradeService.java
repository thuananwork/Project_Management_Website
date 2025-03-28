package com.manager.service;
import com.manager.DTO.GradeDTO;
import com.manager.DTO.ProjectGradeDTO;
import com.manager.model.User;
import com.manager.repository.EvaluationRepository;
import com.manager.repository.ProjectGradeRepository;
import com.manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProjectGradeService {
    @Autowired
    private EvaluationRepository evaluationRepository;
    @Autowired
    private ProjectGradeRepository projectGradeRepository;
    @Autowired
    private UserRepository userRepository;

    public List<ProjectGradeDTO> getAllProjectGrade() {
        List<Object[]> results = projectGradeRepository.findAllProject();
        List<ProjectGradeDTO> projectGradeDTOS = new ArrayList<>();

        for (Object[] result : results) {
            ProjectGradeDTO custom = new ProjectGradeDTO(
                    ((Number) result[0]).longValue(),
                    (String) result[1],
                    (String) result[2],
                    (Date) result[3],
                    (Date) result[4],
                    (String) result[5],
                    (String) result[6]
            );
            projectGradeDTOS.add(custom);
        }
        return projectGradeDTOS;
    }

    public List<GradeDTO> getAllGrade() {
        List<Object[]> results = projectGradeRepository.findAllGrade();
        List<GradeDTO> gradeDTOS = new ArrayList<>();

        for (Object[] result : results) {
            GradeDTO custom = new GradeDTO(
                    ((Number) result[0]).longValue(),
                    (String) result[1],
                    (String) result[2],
                    (Date) result[3],
                    (Date) result[4],
                    (String) result[5],
                    (String) result[6],
                    (BigDecimal) result[7],
                    ((Number) result[8]).longValue()
            );
            gradeDTOS.add(custom);
        }
        return gradeDTOS;
    }

    public User getAllProjectGradeStudent(Long id) {
        Long results = projectGradeRepository.findAllProjectByStudentID(id);
        if (results != null) {
            return userRepository.findById(results)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("No student found for the given project ID");
    }
}
